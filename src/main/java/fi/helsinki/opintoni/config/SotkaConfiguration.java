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
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.sotka.SotkaMockClient;
import fi.helsinki.opintoni.integration.sotka.SotkaRestClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class SotkaConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate sotkaRestTemplate() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = new RestTemplate(Collections.singletonList(converter));
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    @ConditionalOnExpression("'${sotka.client.implementation}' == 'rest'")
    public SotkaClient sotkaRestClient(@Autowired AppConfiguration appConfiguration) {
        return new SotkaRestClient(appConfiguration.get("sotka.base.url"), sotkaRestTemplate());
    }

    @Bean
    @ConditionalOnExpression("'${sotka.client.implementation}' == 'mock'")
    public SotkaClient sotkaMockClient() {
        return new SotkaMockClient(objectMapper);
    }
}
