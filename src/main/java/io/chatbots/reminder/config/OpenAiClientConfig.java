package io.chatbots.reminder.config;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties;
import org.springframework.ai.model.openai.autoconfigure.OpenAiCommonProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * Overrides Spring AI's OpenAI autoconfiguration to inject proxy + trust-all SSL.
 * Required for corporate MITM proxies that re-sign TLS certificates.
 */
@Configuration
public class OpenAiClientConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClientConfig.class);

    @Bean
    public OpenAiChatModel openAiChatModel(
            OpenAiCommonProperties commonProperties,
            OpenAiChatProperties chatProperties,
            ToolCallingManager toolCallingManager,
            ObjectProvider<ObservationRegistry> observationRegistryProvider) {

        var proxy = resolveProxy();
        var trustAll = buildTrustAllManager();

        var syncClientBuilder = OpenAIOkHttpClient.builder()
            .fromEnv()
            .apiKey(commonProperties.getApiKey())
            .checkJacksonVersionCompatibility(false)
            .trustManager(trustAll)
            .hostnameVerifier((h, s) -> true);

        var asyncClientBuilder = OpenAIOkHttpClientAsync.builder()
            .fromEnv()
            .apiKey(commonProperties.getApiKey())
            .checkJacksonVersionCompatibility(false)
            .trustManager(trustAll)
            .hostnameVerifier((h, s) -> true);

        if (commonProperties.getBaseUrl() != null && !commonProperties.getBaseUrl().isBlank()) {
            syncClientBuilder.baseUrl(commonProperties.getBaseUrl());
            asyncClientBuilder.baseUrl(commonProperties.getBaseUrl());
        }

        if (proxy != null) {
            log.warn("OpenAI client using proxy {}:{} with SSL validation disabled (corporate MITM proxy)",
                ((InetSocketAddress) proxy.address()).getHostName(),
                ((InetSocketAddress) proxy.address()).getPort());
            syncClientBuilder.proxy(proxy);
            asyncClientBuilder.proxy(proxy);
        }

        try {
            var sslCtx = HttpClientConfig.trustAllSslContext();
            syncClientBuilder.sslSocketFactory(sslCtx.getSocketFactory());
            asyncClientBuilder.sslSocketFactory(sslCtx.getSocketFactory());
        } catch (Exception e) {
            log.error("Failed to configure trust-all SSL for OpenAI client: {}", e.getMessage());
        }

        return OpenAiChatModel.builder()
            .openAiClient(syncClientBuilder.build())
            .openAiClientAsync(asyncClientBuilder.build())
            .options(chatProperties.toOptions())
            .toolCallingManager(toolCallingManager)
            .observationRegistry(observationRegistryProvider.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();
    }

    private static Proxy resolveProxy() {
        var raw = firstNonBlank(
            System.getenv("HTTPS_PROXY"), System.getenv("https_proxy"),
            System.getenv("HTTP_PROXY"),  System.getenv("http_proxy")
        );
        if (raw == null) return null;
        try {
            var uri = URI.create(raw);
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(),
                uri.getPort() > 0 ? uri.getPort() : 8080));
        } catch (Exception e) {
            log.warn("Could not parse proxy URL '{}': {}", raw, e.getMessage());
            return null;
        }
    }

    private static X509TrustManager buildTrustAllManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}
            public void checkServerTrusted(X509Certificate[] c, String a) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        };
    }

    private static String firstNonBlank(String... values) {
        for (var v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
