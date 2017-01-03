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
import fi.helsinki.opintoni.integration.optime.OptimeClient;
import fi.helsinki.opintoni.integration.optime.OptimeMockClient;
import fi.helsinki.opintoni.integration.optime.OptimeRestClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OptimeConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate optimeRestTemplate() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = new RestTemplate(Lists.newArrayList(converter));
        restTemplate.setInterceptors(Lists.newArrayList(new LoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    public OptimeClient optimeMockClient() { return new OptimeMockClient(objectMapper); }

    @Bean
    public OptimeClient optimeRestClient() {
        return new OptimeRestClient(appConfiguration.get("optime.base.url"), optimeRestTemplate());
    }

    @Bean
    public OptimeClient optimeClient() {
        return NamedDelegatesProxy.builder(
            OptimeClient.class,
            () -> appConfiguration.get("optime.client.implementation"))
            .with("rest", optimeRestClient())
            .with("mock", optimeMockClient())
            .build();
    }

}
