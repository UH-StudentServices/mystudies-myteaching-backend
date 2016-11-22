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

package fi.helsinki.opintoni.integration.coursepage;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class CoursePageRestClient implements CoursePageClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CoursePageRestClient.class);

    public CoursePageRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Cacheable(CacheConstants.COURSE_PAGE_ONE_OFF_EVENTS)
    @Override
    public List<CoursePageEvent> getEvents(String courseImplementationId) {
        ResponseEntity<List<CoursePageEvent>> responseEntity =
            restTemplate.exchange("{baseUrl}/events?course_implementation_id={courseImplementationId}", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CoursePageEvent>>() {
                },
                baseUrl, courseImplementationId);

        return Optional.ofNullable(responseEntity.getBody())
            .orElse(newArrayList());
    }

    @Cacheable(value = CacheConstants.COURSE_PAGE, key = "#courseImplementationId")
    @Override
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId) {
        log.info("fetching course impl with id {}", courseImplementationId);

        return
            restTemplate.exchange(
                "{baseUrl}/course_implementations?course_implementation_id={courseImplementationId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CoursePageCourseImplementation>() {
                },
                baseUrl, courseImplementationId).getBody();
    }

    @Override
    public List<CoursePageNotification> getCoursePageNotifications(Set<String> courseImplementationIds,
                                                                   LocalDateTime from,
                                                                   Locale locale) {
        if (courseImplementationIds.isEmpty()) {
            return newArrayList();
        }

        ResponseEntity<List<CoursePageNotification>> responseEntity = restTemplate.exchange(
            "{baseUrl}/course_implementation_activity" +
                "?course_implementation_id={courseImplementationIds}&timestamp={from}&langcode={locale}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CoursePageNotification>>() {
            },
            baseUrl,
            courseImplementationIds.stream().collect(Collectors.joining(",")),
            from.format(DateFormatter.COURSE_PAGE_DATE_TIME_FORMATTER),
            locale.getLanguage());

        return Optional.ofNullable(responseEntity.getBody())
            .orElse(newArrayList());
    }

    @Override
    public List<Long> getUpdatedCourseImplementationIds(long timestamp) {
        ResponseEntity<List<Long>> response = restTemplate.exchange(
            "{baseUrl}/course_implementation/changes/since/{timestamp}", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Long>>() {}, baseUrl, timestamp);

        return Optional.ofNullable(response.getBody()).orElse(newArrayList());
    }
}
