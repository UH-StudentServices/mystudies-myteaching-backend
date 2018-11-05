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
import fi.helsinki.opintoni.integration.guide.GuideClient;
import fi.helsinki.opintoni.integration.guide.GuideMockClient;
import fi.helsinki.opintoni.integration.guide.GuideRestClient;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.*;

@Configuration
public class GuideConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate guideRestTemplate() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate =  new RestTemplate(singletonList(converter));
        restTemplate.setInterceptors(singletonList(new LoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    public GuideClient guideMockClient() {
        return new GuideMockClient(objectMapper);
    }

    @Bean
    public GuideClient guideRestClient() {
        return new GuideRestClient(appConfiguration.get("guide.base.url"), guideRestTemplate());
    }

    @Bean
    public GuideClient guideClient() {
        return NamedDelegatesProxy.builder(
            GuideClient.class,
            () -> appConfiguration.get("guide.client.implementation"))
            .with("rest", guideRestClient())
            .with("mock", guideMockClient())
            .build();
    }

}
