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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CoursePageRestClient implements CoursePageClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CoursePageRestClient.class);

    public CoursePageRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public <T> List<T> getCoursePageData(
        String path,
        ParameterizedTypeReference<List<T>> typeReference,
        Object... uriVariables) {

        try {
            return restTemplate.exchange(baseUrl + path, HttpMethod.GET, null, typeReference, uriVariables).getBody();
        } catch (Exception e) {
            log.error("Caught exception when calling Course Pages:", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Cacheable(value = CacheConstants.COURSE_PAGE, key = "#courseImplementationId")
    @Override
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId) {
        log.trace("fetching course impl with id {}", courseImplementationId);

        List<CoursePageCourseImplementation> coursePageCourseImplementationList = getCoursePageData(
            "/course_implementation/{courseImplementationId}",
            new ParameterizedTypeReference<List<CoursePageCourseImplementation>>() {},
            courseImplementationId);

        if(coursePageCourseImplementationList != null && coursePageCourseImplementationList.size() > 0) {
            return coursePageCourseImplementationList.get(0);
        } else {
            return new CoursePageCourseImplementation();
        }
    }

    @Override
    public List<CoursePageNotification> getCoursePageNotifications(Set<String> courseImplementationIds,
                                                                   LocalDateTime from,
                                                                   Locale locale) {
        if (courseImplementationIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<CoursePageNotification> notifications = getCoursePageData(
            "/course_implementation_activity?course_implementation_id={courseImplementationIds}&timestamp={from}&langcode={locale}",
            new ParameterizedTypeReference<List<CoursePageNotification>>() {},
            courseImplementationIds.stream().collect(Collectors.joining(",")),
            from.format(DateFormatter.COURSE_PAGE_DATE_TIME_FORMATTER),
            locale.getLanguage());

        if(notifications != null) {
            return notifications;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getUpdatedCourseImplementationIds(long timestamp) {
        List<Long> implementationIds = getCoursePageData(
            "/course_implementation/changes/since/{timestamp}",
            new ParameterizedTypeReference<List<Long>>() {}, timestamp);

        if(implementationIds != null) {
            return implementationIds;
        } else {
            return new ArrayList<>();
        }
    }
}
