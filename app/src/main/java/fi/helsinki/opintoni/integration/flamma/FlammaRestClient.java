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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;

public class FlammaRestClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    private final ImmutableMap<String, String> studentFeedsByLocale;
    private final ImmutableMap<String, String> teacherFeedsByLocale;
    private final ImmutableMap<String, String> studentOpenUniversityFeedsByLocale;

    public FlammaRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;

        studentFeedsByLocale = ImmutableMap.of(
            "fi", "atom-tiedotteet-opiskelijalle.xml",
            "sv", "atom-tiedotteet-opiskelijalle-sv.xml",
            "en", "atom-tiedotteet-opiskelijalle-en.xml");
        teacherFeedsByLocale = ImmutableMap.of(
            "fi", "atom-tiedotteet-opetusasiat.xml",
            "sv", "atom-tiedotteet-opetusasiat-sv.xml",
            "en", "atom-tiedotteet-opetusasiat-en.xml");
        studentOpenUniversityFeedsByLocale = ImmutableMap.of(
            "fi", "atom-tiedotteet-avoin.xml",
            "sv", "atom-tiedotteet-avoin-sv.xml",
            "en", "atom-tiedotteet-avoin-en.xml");
    }

    public Feed getStudentFeed(Locale locale) {
        String uri = getFeedUri(studentFeedsByLocale.get(locale.getLanguage()));
        return restTemplate.getForObject(uri, Feed.class);
    }

    public Feed getTeacherFeed(Locale locale) {
        String uri = getFeedUri(teacherFeedsByLocale.get(locale.getLanguage()));
        return restTemplate.getForObject(uri, Feed.class);
    }

    public Feed getStudentOpenUniversityFeed(Locale locale) {
        String uri = getFeedUri(studentOpenUniversityFeedsByLocale.get(locale.getLanguage()));
        return restTemplate.getForObject(uri, Feed.class);
    }

    private String getFeedUri(String pathSegment) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("infotaulu")
            .pathSegment(pathSegment)
            .toUriString();
    }
}
