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
import fi.helsinki.opintoni.integration.unisport.UnisportClient;
import fi.helsinki.opintoni.integration.unisport.UnisportMockClient;
import fi.helsinki.opintoni.integration.unisport.UnisportRestClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class UnisportConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate unisportRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getConverters());
        restTemplate.setInterceptors(Lists.newArrayList(new LoggingInterceptor()));
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return Lists.newArrayList(converter);
    }

    @Bean
    public UnisportClient unisportMockClient() {
        return new UnisportMockClient();
    }

    @Bean
    public UnisportClient unisportRestClient() {
        return new UnisportRestClient(appConfiguration.get("unisport.base.url"), unisportRestTemplate());
    }

    @Bean
    public UnisportClient unisportClient() {
        return NamedDelegatesProxy.builder(
            UnisportClient.class,
            () -> appConfiguration.get("unisport.client.implementation"))
            .with("rest", unisportRestClient())
            .with("mock", unisportMockClient())
            .build();
    }
}
