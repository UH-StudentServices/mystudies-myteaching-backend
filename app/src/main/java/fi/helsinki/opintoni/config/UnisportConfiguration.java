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
import fi.helsinki.opintoni.integration.unisport.ExpiringUnisportJWTService;
import fi.helsinki.opintoni.integration.unisport.MockUnisportJWTService;
import fi.helsinki.opintoni.integration.unisport.UnisportClient;
import fi.helsinki.opintoni.integration.unisport.UnisportJWTService;
import fi.helsinki.opintoni.integration.unisport.UnisportMockClient;
import fi.helsinki.opintoni.integration.unisport.UnisportRestClient;
import fi.helsinki.opintoni.service.converter.favorite.UnisportFavoriteConverter;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        return new UnisportRestClient(appConfiguration.get("unisport.base.url"), unisportRestTemplate(), unisportJWTService());
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

    @Bean
    public ExpiringUnisportJWTService expiringUnisportJWTService() {
        return new ExpiringUnisportJWTService(
            getKey(),
            SignatureAlgorithm.HS256,
            appConfiguration.getInteger("unisport.tokenValidForSeconds"));
    }

    @Bean
    MockUnisportJWTService mockUnisportJWTService() {
        return new MockUnisportJWTService();
    }

    @Bean
    public UnisportJWTService unisportJWTService() {
        return NamedDelegatesProxy.builder(
            UnisportJWTService.class,
            () -> appConfiguration.get("unisport.jwt.service.implementation"))
            .with("expiring", expiringUnisportJWTService())
            .with("mock", mockUnisportJWTService())
            .build();
    }

    @Bean
    UnisportFavoriteConverter unisportFavoriteConverter() {
        return new UnisportFavoriteConverter(appConfiguration.get("unisport.userAuthorizationUrl"));
    }

    private Key getKey() {
        return new SecretKeySpec(appConfiguration.get("unisportApiKey").getBytes(UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }
}
