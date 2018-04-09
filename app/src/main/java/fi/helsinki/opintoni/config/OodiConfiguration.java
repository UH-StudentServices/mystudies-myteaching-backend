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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.security.KeyStoreException;
import java.util.List;

@Configuration
public class OodiConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

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

    private KeyStore oodiKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileSystemResource keystoreFile = new FileSystemResource(
                new File(appConfiguration.get("oodi.keystoreLocation")));

            keyStore.load(keystoreFile.getInputStream(), appConfiguration.get("oodi.keystorePassword").toCharArray());
            return keyStore;
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to instantiate keystore");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Oodi keystore");
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(appConfiguration.getInteger("httpClient.readTimeout"));
        factory.setConnectTimeout(appConfiguration.getInteger("httpClient.connectTimeout"));

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
            new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(appConfiguration.getInteger("httpClient.maxTotal"));
        poolingHttpClientConnectionManager
            .setDefaultMaxPerRoute(appConfiguration.getInteger("httpClient.defaultMaxPerRoute"));

        try {
            SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(oodiKeyStore(), appConfiguration.get("oodi.keystorePassword").toCharArray()).build();

            CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setSSLContext(sslContext)
                .build();

            factory.setHttpClient(httpClient);

            return new BufferingClientHttpRequestFactory(factory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Oodi key material.");
        }
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
