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

import java.io.File;
import java.security.KeyStore;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import fi.helsinki.opintoni.config.http.SSLRequestFactory;
import fi.helsinki.opintoni.exception.SSLContextException;
import fi.helsinki.opintoni.integration.interceptor.HttpRequestInterceptor;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiClient;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiRestClient;
import fi.helsinki.opintoni.integration.studyregistry.oodi.mock.OodiMockClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuGraphQLClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.mock.SisuMockClient;

@Configuration
public class StudyRegistryConfiguration {

    private final AppConfiguration appConfiguration;
    private final Environment env;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(StudyRegistryConfiguration.class);

    @Value("${httpClient.keystoreLocation:null}")
    private String keystoreLocation;

    @Value("${httpClient.keystorePassword:null}")
    private String keystorePassword;

    @Value("${oodi.useHttpClientCertificate:false}")
    private boolean useHttpClientCertificate;

    @Value("${sisu.baseUrl}")
    private String sisuBaseUrl;

    @Value("${sisu.apiPath}")
    private String sisuApiPath;

    @Autowired
    public StudyRegistryConfiguration(AppConfiguration appConfiguration, Environment env, ObjectMapper objectMapper) {
        this.appConfiguration = appConfiguration;
        this.env = env;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initDefaultSSLContext() {
        SSLContext sslContext = sslContext();

        if (sslContext != null) {
            SSLContext.setDefault(sslContext);
        }
    }

    @Bean
    @Primary
    public SSLContext sslContext() {
        if (useHttpClientCertificate && keystoreLocation != null && keystorePassword != null) {
            return createSslContextWithClientCertificate(keystoreLocation, keystorePassword);
        }

        return null;
    }

    @Bean
    public RestTemplate oodiRestTemplate() {
        log.info(String.format("OodiConfiguration.keystoreLocation=%s", keystoreLocation == null ? "NULL" : keystoreLocation));
        RestTemplate restTemplate = new RestTemplate(SSLRequestFactory.clientHttpRequestFactory(
            appConfiguration, sslContext()));

        restTemplate.setInterceptors(Lists.newArrayList(
            new LoggingInterceptor(),
            new HttpRequestInterceptor(objectMapper, env)
        ));
        restTemplate.setMessageConverters(getConverters());
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return Lists.newArrayList(converter);
    }

    @Bean
    @ConditionalOnExpression("'${oodi.client.implementation}' == 'mock'")
    public OodiClient oodiMockClient() {
        return new OodiMockClient(objectMapper);
    }

    @Bean
    @ConditionalOnExpression("'${oodi.client.implementation}' == 'rest'")
    public OodiClient oodiRestClient() {
        return new OodiRestClient(appConfiguration.get("oodi.base.url"), oodiRestTemplate());
    }

    @Bean
    @ConditionalOnExpression("'${sisu.client.implementation}' == 'graphQL'")
    public SisuGraphQLClient sisuGraphQLClient() {
        RestTemplate restTemplate = new RestTemplate(SSLRequestFactory.clientHttpRequestFactory(
            appConfiguration, sslContext()));
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(Lists.newArrayList(
            new LoggingInterceptor()
        ));

        return new SisuGraphQLClient(sisuBaseUrl + sisuApiPath, restTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${sisu.client.implementation}' == 'mock'")
    public SisuClient sisuMockClient() {
        return new SisuMockClient();
    }

    private SSLContext createSslContextWithClientCertificate(String keystoreLocation, String keystorePassword) {
        char[] keystorePasswordCharArray = keystorePassword.toCharArray();

        try {
            return SSLContextBuilder
                .create()
                .loadKeyMaterial(keyStore(keystoreLocation, keystorePasswordCharArray), keystorePasswordCharArray)
                .build();
        } catch (Exception e) {
            throw new SSLContextException("Failed to load client keystore", e);
        }
    }

    private static KeyStore keyStore(String keystoreLocation, char[] keystorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileSystemResource keystoreFile = new FileSystemResource(new File(keystoreLocation));

            keyStore.load(keystoreFile.getInputStream(), keystorePassword);
            return keyStore;
        } catch (Exception e) {
            throw new SSLContextException(String.format("Failed to load client keystore from '%s'", keystoreLocation), e);
        }
    }
}
