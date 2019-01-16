package fi.helsinki.opintoni.config.http;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;

public class SSLRequestFactory {

    public static ClientHttpRequestFactory clientHttpRequestFactory(AppConfiguration appConfiguration,
                                                                    boolean useHttpClientCertificate,
                                                                    String keystoreLocation,
                                                                    String keystorePassword) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(appConfiguration.getInteger("httpClient.readTimeout"));
        factory.setConnectTimeout(appConfiguration.getInteger("httpClient.connectTimeout"));

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = poolingHttpClientConnectionManager(
            useHttpClientCertificate, keystoreLocation, keystorePassword);

        poolingHttpClientConnectionManager.setMaxTotal(appConfiguration.getInteger("httpClient.maxTotal"));
        poolingHttpClientConnectionManager
            .setDefaultMaxPerRoute(appConfiguration.getInteger("httpClient.defaultMaxPerRoute"));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .build();

        factory.setHttpClient(httpClient);

        return new BufferingClientHttpRequestFactory(factory);
    }

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(boolean useHttpClientCertificate,
                                                                                         String keystoreLocation,
                                                                                         String keystorePassword) {
        if (useHttpClientCertificate && keystoreLocation != null && keystorePassword != null) {
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext(keystoreLocation, keystorePassword));
            Registry socketFactoryRegistry = RegistryBuilder.create().register("https", sslConnectionSocketFactory).build();

            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            return new PoolingHttpClientConnectionManager();
        }
    }

    private static SSLContext sslContext(String keystoreLocation, String keystorePassword) {
        char[] keystorePasswordCharArray = keystorePassword.toCharArray();

        try {
            return SSLContextBuilder.create()
                .loadKeyMaterial(keyStore(keystoreLocation, keystorePasswordCharArray), keystorePasswordCharArray).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load client keystore");
        }
    }

    private static KeyStore keyStore(String keystoreLocation, char[] keystorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileSystemResource keystoreFile = new FileSystemResource(
            new File(keystoreLocation));

        keyStore.load(keystoreFile.getInputStream(), keystorePassword);
        return keyStore;
    }
}
