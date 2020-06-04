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
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsMockClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsRestClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

@Configuration
public class CourseCmsConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate courseCmsRestTemplate() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = new RestTemplate(singletonList(converter));
        restTemplate.setInterceptors(singletonList(new LoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    public CourseCmsClient courseCmsRestClient() {
        return new CourseCmsRestClient(appConfiguration.get("courseCms.base.url"), courseCmsRestTemplate());
    }

    @Bean
    public CourseCmsClient courseCmsMockClient() {
        return new CourseCmsMockClient(objectMapper);
    }

    @Bean
    public CourseCmsClient courseCmsClient() {
        return NamedDelegatesProxy.builder(
            CourseCmsClient.class,
            () -> appConfiguration.get("courseCms.client.implementation"))
            .with("rest", courseCmsRestClient())
            .with("mock", courseCmsMockClient())
            .build();
    }
}
