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

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaClient;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaMockClient;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaRestClient;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsClient;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsMockClient;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsRestClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

@Configuration
@ConfigurationProperties(prefix = "newsfeeds")
public class NewsFeedConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    private Map<String, String> studentFeedsByLocale;
    private Map<String, String> teacherFeedsByLocale;
    private Map<String, String> guideFeedsByLocale;

    private RestTemplate createRestTemplate() {
        final AtomFeedHttpMessageConverter converter = new AtomFeedHttpMessageConverter();
        List<MediaType> mediaTypes = ImmutableList.of(
            MediaType.TEXT_XML,
            new MediaType("application", "rss+xml"));

        converter.setSupportedMediaTypes(mediaTypes);

        RestTemplate restTemplate = new RestTemplate(singletonList(converter));

        restTemplate.setInterceptors(singletonList(new LoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    public FlammaClient flammaMockClient() {
        return new FlammaMockClient();
    }

    @Bean
    public FlammaClient flammaRestClient() {
        return new FlammaRestClient(createRestTemplate(), studentFeedsByLocale, teacherFeedsByLocale);
    }

    @Bean
    public GuideNewsClient guideNewsMockClient() {
        return new GuideNewsMockClient();
    }

    @Bean
    public GuideNewsClient guideNewsRestClient() {
        return new GuideNewsRestClient(createRestTemplate(), guideFeedsByLocale);
    }

    @Bean
    public GuideNewsClient guideNewsClient() {
        return NamedDelegatesProxy.builder(
            GuideNewsClient.class,
            () -> appConfiguration.get("newsfeeds.client.implementation"))
            .with("rest", guideNewsRestClient())
            .with("mock", guideNewsMockClient())
            .build();
    }

    @Bean
    public FlammaClient flammaClient() {
        return NamedDelegatesProxy.builder(
            FlammaClient.class,
            () -> appConfiguration.get("newsfeeds.client.implementation"))
            .with("rest", flammaRestClient())
            .with("mock", flammaMockClient())
            .build();
    }

    //
    // Following getters and setters needed for @ConfigurationProperties to inject
    // the maps from configuration files. Please don't remove even though they may seem unused.
    //
    public Map<String, String> getStudentFeedsByLocale() {
        return studentFeedsByLocale;
    }

    public void setStudentFeedsByLocale(
        Map<String, String> studentFeedsByLocale) {
        this.studentFeedsByLocale = studentFeedsByLocale;
    }

    public Map<String, String> getTeacherFeedsByLocale() {
        return teacherFeedsByLocale;
    }

    public void setTeacherFeedsByLocale(
        Map<String, String> teacherFeedsByLocale) {
        this.teacherFeedsByLocale = teacherFeedsByLocale;
    }

    public Map<String, String> getGuideFeedsByLocale() {
        return guideFeedsByLocale;
    }

    public void setGuideFeedsByLocale(
        Map<String, String> guideFeedsByLocale) {
        this.guideFeedsByLocale = guideFeedsByLocale;
    }

}
