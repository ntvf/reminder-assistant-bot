package io.chatbots.reminder;

import io.chatbots.reminder.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.cert.X509Certificate;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ReminderApplication {

    private static final Logger log = LoggerFactory.getLogger(ReminderApplication.class);

    public static void main(String[] args) {
        configureProxy();
        SpringApplication.run(ReminderApplication.class, args);
    }

    private static void configureProxy() {
        var raw = firstNonBlank(
            System.getenv("HTTPS_PROXY"), System.getenv("https_proxy"),
            System.getenv("HTTP_PROXY"),  System.getenv("http_proxy")
        );
        if (raw == null) return;

        try {
            var uri = URI.create(raw);
            var host = uri.getHost();
            var port = String.valueOf(uri.getPort() > 0 ? uri.getPort() : 8080);

            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", port);
            System.setProperty("http.proxyHost",  host);
            System.setProperty("http.proxyPort",  port);

            var noProxy = firstNonBlank(System.getenv("NO_PROXY"), System.getenv("no_proxy"));
            if (noProxy != null) System.setProperty("http.nonProxyHosts", noProxy.replace(",", "|"));

            log.warn("Corporate proxy detected ({}:{}). Disabling SSL certificate validation.", host, port);
            installTrustAllSsl();
        } catch (Exception e) {
            log.warn("Could not configure proxy from env: {}", e.getMessage());
        }
    }

    private static void installTrustAllSsl() throws Exception {
        var trustAll = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}
            public void checkServerTrusted(X509Certificate[] c, String a) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        };
        var ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{trustAll}, null);
        SSLContext.setDefault(ctx);
    }

    private static String firstNonBlank(String... values) {
        for (var v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
