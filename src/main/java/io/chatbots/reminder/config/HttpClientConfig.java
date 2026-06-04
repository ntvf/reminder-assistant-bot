package io.chatbots.reminder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

@Configuration
public class HttpClientConfig {

    private static final Logger log = LoggerFactory.getLogger(HttpClientConfig.class);

    @Bean
    public RestClientCustomizer proxyRestClientCustomizer() {
        return builder -> {
            var httpClient = buildHttpClient();
            builder.requestFactory(new JdkClientHttpRequestFactory(httpClient));
        };
    }

    private static HttpClient buildHttpClient() {
        var clientBuilder = HttpClient.newBuilder();

        // Always disable SSL validation — corporate MITM proxy re-signs certificates
        try {
            clientBuilder.sslContext(trustAllSslContext());
        } catch (Exception e) {
            log.error("Failed to configure trust-all SSL for RestClient: {}", e.getMessage());
        }

        var proxy = resolveProxy();
        if (proxy != null) {
            clientBuilder.proxy(ProxySelector.of(proxy));
            log.warn("RestClient using proxy {}:{} with SSL validation disabled (corporate MITM proxy)",
                proxy.getHostName(), proxy.getPort());
        }

        return clientBuilder.build();
    }

    private static InetSocketAddress resolveProxy() {
        var raw = firstNonBlank(
            System.getenv("HTTPS_PROXY"), System.getenv("https_proxy"),
            System.getenv("HTTP_PROXY"),  System.getenv("http_proxy")
        );
        if (raw == null) return null;
        try {
            var uri = URI.create(raw);
            return new InetSocketAddress(uri.getHost(), uri.getPort() > 0 ? uri.getPort() : 8080);
        } catch (Exception e) {
            log.warn("Could not parse proxy URL '{}': {}", raw, e.getMessage());
            return null;
        }
    }

    static SSLContext trustAllSslContext() throws Exception {
        var trustAll = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}
            public void checkServerTrusted(X509Certificate[] c, String a) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        };
        var ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{trustAll}, null);
        return ctx;
    }

    private static String firstNonBlank(String... values) {
        for (var v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
