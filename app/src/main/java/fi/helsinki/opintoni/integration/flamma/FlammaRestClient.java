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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Locale;

@Component
public class FlammaRestClient {
    private final static Logger log = LoggerFactory.getLogger(FlammaRestClient.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    private AppConfiguration appConfiguration;

    private final ImmutableMap<String, String> studentFeedsByLocale;
    private final ImmutableMap<String, String> teacherFeedsByLocale;

    @Autowired
    public FlammaRestClient(AppConfiguration appConfiguration) {
        this.baseUrl = appConfiguration.get("flamma.base.url");
        this.restTemplate = createRestTemplate();

        studentFeedsByLocale = ImmutableMap.of(
            "fi", "atom-tiedotteet-opiskelijalle.xml",
            "sv", "atom-tiedotteet-opiskelijalle-sv.xml",
            "en", "atom-tiedotteet-opiskelijalle-en.xml");
        teacherFeedsByLocale = ImmutableMap.of(
            "fi", "atom-tiedotteet-opetusasiat.xml",
            "sv", "atom-tiedotteet-opetusasiat-sv.xml",
            "en", "atom-tiedotteet-opetusasiat-en.xml");
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public Feed getStudentFeed(Locale locale) {
        String uri = getFeedUri(studentFeedsByLocale.get(locale.getLanguage()));
        return restTemplate.getForObject(uri, Feed.class);
    }

    public Feed getTeacherFeed(Locale locale) {
        String uri = getFeedUri(teacherFeedsByLocale.get(locale.getLanguage()));
        return restTemplate.getForObject(uri, Feed.class);
    }

    private RestTemplate createRestTemplate() {
        final AtomFeedHttpMessageConverter converter = new AtomFeedHttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_XML));

        return new RestTemplate(Collections.singletonList(converter));
    }

    private String getFeedUri(String pathSegment) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("infotaulu")
            .pathSegment(pathSegment)
            .toUriString();
    }
}
