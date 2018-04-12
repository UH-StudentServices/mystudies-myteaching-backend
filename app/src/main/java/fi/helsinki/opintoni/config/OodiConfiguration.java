/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.integration.interceptor.OodiExceptionInterceptor;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiRestClient;
import fi.helsinki.opintoni.integration.oodi.mock.OodiMockClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class OodiConfiguration {
    private static final Logger logger = getLogger(OodiConfiguration.class);

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${httpClient.keystoreLocation:null}")
    private String keystoreLocation;

    @Value("${httpClient.keystorePassword:null}")
    private String keystorePassword;

    @Value("${oodi.useHttpClientCertificate:false}")
    private boolean useHttpClientCertificate;

    @Bean
    public RestTemplate oodiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setInterceptors(Lists.newArrayList(
            new LoggingInterceptor(),
            new OodiExceptionInterceptor(objectMapper, env)
        ));
        restTemplate.setMessageConverters(getConverters());
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return Lists.newArrayList(converter);
    }

    private KeyStore oodiKeyStore(String keystoreLocation, char[] keystorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileSystemResource keystoreFile = new FileSystemResource(
            new File(keystoreLocation));

        keyStore.load(keystoreFile.getInputStream(), keystorePassword);
        return keyStore;
    }

    private SSLContext sslContext(String keystoreLocation, String keystorePassword) {
        char[] keystorePasswordCharArray = keystorePassword.toCharArray();

        try {
            return SSLContextBuilder.create()
                .loadKeyMaterial(oodiKeyStore(keystoreLocation, keystorePasswordCharArray), keystorePasswordCharArray).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load client keystore");
        }
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        if (useHttpClientCertificate && keystoreLocation != null && keystorePassword != null) {
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext(keystoreLocation, keystorePassword));
            Registry socketFactoryRegistry = RegistryBuilder.create().register("https", sslConnectionSocketFactory).build();

            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            return new PoolingHttpClientConnectionManager();
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(appConfiguration.getInteger("httpClient.readTimeout"));
        factory.setConnectTimeout(appConfiguration.getInteger("httpClient.connectTimeout"));

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = poolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(appConfiguration.getInteger("httpClient.maxTotal"));
        poolingHttpClientConnectionManager
            .setDefaultMaxPerRoute(appConfiguration.getInteger("httpClient.defaultMaxPerRoute"));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .build();

        factory.setHttpClient(httpClient);

        return new BufferingClientHttpRequestFactory(factory);
    }

    @Bean
    public OodiClient oodiMockClient() {
        return new OodiMockClient(objectMapper);
    }

    @Bean
    public OodiClient oodiRestClient() {
        return new OodiRestClient(appConfiguration.get("oodi.base.url"), oodiRestTemplate());
    }

    @Bean
    public OodiClient oodiClient() {
        return NamedDelegatesProxy.builder(
            OodiClient.class,
            () -> appConfiguration.get("oodi.client.implementation"))
            .with("rest", oodiRestClient())
            .with("mock", oodiMockClient())
            .build();
    }
}
