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

package fi.helsinki.opintoni.integration.flamma;

import com.google.common.collect.ImmutableMap;
import com.rometools.rome.feed.atom.Feed;
import fi.helsinki.opintoni.config.AppConfiguration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Locale;

@Component
@ConfigurationProperties(prefix = "flamma")
public class FlammaRestClient {
    private final static Logger log = LoggerFactory.getLogger(FlammaRestClient.class);

    private final RestTemplate restTemplate;

    private Map<String, String> studentFeedsByLocale;
    private Map<String, String> teacherFeedsByLocale;

    @Autowired
    public FlammaRestClient() {
        this.restTemplate = createRestTemplate();
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public Feed getStudentFeed(Locale locale) {
        return getFeed(studentFeedsByLocale.get(locale.getLanguage()));
    }

    public Feed getTeacherFeed(Locale locale) {
        return getFeed(teacherFeedsByLocale.get(locale.getLanguage()));
    }

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

    private RestTemplate createRestTemplate() {
        final AtomFeedHttpMessageConverter converter = new AtomFeedHttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_XML));

        return new RestTemplate(Collections.singletonList(converter));
    }

    private Feed getFeed(String uri) {
        try {
            return restTemplate.getForObject(uri, Feed.class);
        } catch (RestClientException e) {
            log.error("Flamma REST client with uri {} threw exception: {}", uri, e.getMessage());
            return new Feed("atom_0.3");
        }
    }
}
