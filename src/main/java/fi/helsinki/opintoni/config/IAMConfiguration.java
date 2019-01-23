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
import fi.helsinki.opintoni.config.http.SSLRequestFactory;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.integration.iam.IAMMockClient;
import fi.helsinki.opintoni.integration.iam.IAMRestClient;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
public class IAMConfiguration {

    @Value("${httpClient.keystoreLocation:null}")
    private String keystoreLocation;

    @Value("${httpClient.keystorePassword:null}")
    private String keystorePassword;

    @Value("${iam.useHttpClientCertificate:false}")
    private boolean useHttpClientCertificate;

    private final AppConfiguration appConfiguration;
    private final ObjectMapper objectMapper;

    @Autowired
    public IAMConfiguration(AppConfiguration appConfiguration, ObjectMapper objectMapper) {
        this.appConfiguration = appConfiguration;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestTemplate iamRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(SSLRequestFactory.clientHttpRequestFactory(
            appConfiguration, useHttpClientCertificate, keystoreLocation, keystorePassword));
        restTemplate.setMessageConverters(getConverters());
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return Collections.singletonList(converter);
    }

    @Bean
    public IAMClient iamMockClient() {
        return new IAMMockClient();
    }

    @Bean
    public IAMClient iamRestClient() {
        return new IAMRestClient(appConfiguration.get("iam.base.url"), iamRestTemplate());
    }

    @Bean
    public IAMClient iamClient() {
        return NamedDelegatesProxy.builder(IAMClient.class, () -> appConfiguration.get("iam.client.implementation"))
            .with("mock", iamMockClient())
            .with("rest", iamRestClient())
            .build();
    }
}
