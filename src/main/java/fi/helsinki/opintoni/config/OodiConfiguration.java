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
import fi.helsinki.opintoni.config.http.SSLRequestFactory;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.integration.interceptor.OodiExceptionInterceptor;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiRestClient;
import fi.helsinki.opintoni.integration.oodi.mock.OodiMockClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class OodiConfiguration {

    private final AppConfiguration appConfiguration;
    private final Environment env;
    private final ObjectMapper objectMapper;

    @Value("${httpClient.keystoreLocation:null}")
    private String keystoreLocation;

    @Value("${httpClient.keystorePassword:null}")
    private String keystorePassword;

    @Value("${oodi.useHttpClientCertificate:false}")
    private boolean useHttpClientCertificate;

    @Autowired
    public OodiConfiguration(AppConfiguration appConfiguration, Environment env, ObjectMapper objectMapper) {
        this.appConfiguration = appConfiguration;
        this.env = env;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestTemplate oodiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(SSLRequestFactory.clientHttpRequestFactory(
            appConfiguration, useHttpClientCertificate, keystoreLocation, keystorePassword));

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
