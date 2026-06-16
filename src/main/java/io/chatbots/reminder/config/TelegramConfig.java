package io.chatbots.reminder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;

import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.cert.X509Certificate;

@Configuration
public class TelegramConfig {

    private static final Logger log = LoggerFactory.getLogger(TelegramConfig.class);

    @Bean
    public OkHttpClient telegramOkHttpClient() {
        var proxy = resolveProxy();
        if (proxy == null) {
            return new TelegramOkHttpClientFactory.DefaultOkHttpClientCreator().get();
        }

        var base = new TelegramOkHttpClientFactory.HttpProxyOkHttpClientCreator(
            () -> proxy,
            () -> okhttp3.Authenticator.NONE
        ).get();

        return trustAllSsl(base.newBuilder()).build();
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication(OkHttpClient telegramOkHttpClient) {
        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> telegramOkHttpClient);
    }

    private static Proxy resolveProxy() {
        var raw = firstNonBlank(
            System.getenv("HTTPS_PROXY"),
            System.getenv("https_proxy"),
            System.getenv("HTTP_PROXY"),
            System.getenv("http_proxy")
        );
        if (raw == null) return null;

        try {
            var uri = URI.create(raw);
            var host = uri.getHost();
            var port = uri.getPort() > 0 ? uri.getPort() : 8080;
            var proxyType = "socks".equalsIgnoreCase(uri.getScheme()) ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
            log.warn("Telegram using {} proxy {}:{} with SSL validation disabled (corporate MITM proxy)",
                proxyType, host, port);
            return new Proxy(proxyType, new InetSocketAddress(host, port));
        } catch (Exception e) {
            log.warn("Could not parse proxy URL '{}': {}", raw, e.getMessage());
            return null;
        }
    }

    private static String firstNonBlank(String... values) {
        for (var v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static OkHttpClient.Builder trustAllSsl(OkHttpClient.Builder builder) {
        try {
            var ctx = HttpClientConfig.trustAllSslContext();
            var trustAll = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            };
            return builder
                .sslSocketFactory(ctx.getSocketFactory(), trustAll)
                .hostnameVerifier((h, s) -> true);
        } catch (Exception e) {
            log.error("Failed to configure trust-all SSL: {}", e.getMessage());
            return builder;
        }
    }
}
