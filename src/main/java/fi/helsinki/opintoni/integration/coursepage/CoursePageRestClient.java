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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.cache.CacheConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class CoursePageRestClient implements CoursePageClient {
    private final String baseUrl;
    private final String apiPath;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CoursePageRestClient.class);

    // Course Pages server only returns max. 10 course implementation per call.
    private static final int COURSE_IMPLEMENTATION_BATCH_SIZE = 10;

    public CoursePageRestClient(String baseUrl, String apiPath, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.apiPath = apiPath;
        this.restTemplate = restTemplate;
    }

    public <T> List<T> getCoursePageData(
        String path,
        ParameterizedTypeReference<List<T>> typeReference,
        Object... uriVariables) {

        return getCoursePageData(path, typeReference, null, uriVariables);
    }

    private <T> List<T> getCoursePageData(
        String path,
        ParameterizedTypeReference<List<T>> typeReference,
        Locale locale,
        Object... uriVariables) {
        String url = getCoursePageApiUrl(path, locale);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody();
        } catch (Exception e) {
            log.error("Caught exception when calling Course Pages URL " + url, e);
            throw new CoursePageIntegrationException(e.getMessage(), e);
        }
    }

    private String getCoursePageApiUrl(String path, Locale locale) {
        String localeUrlSegment = locale != null ? "/" + locale.getLanguage() : "";

        return baseUrl + localeUrlSegment + apiPath + path;
    }

    @Override
    @Cacheable(
        value = CacheConstants.COURSE_PAGE,
        key = "#courseImplementationId + '_' + #locale.getLanguage()",
        unless = "#result.courseImplementationId == null",
        cacheManager = "persistentCacheManager")
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId, Locale locale) {
        log.trace("fetching course impl with id {} and locale {}", courseImplementationId, locale.toString());

        try {
            List<CoursePageCourseImplementation> coursePageCourseImplementationList =
                    getCoursePages(singletonList(courseImplementationId), locale);

            if (coursePageCourseImplementationList != null) {
                return coursePageCourseImplementationList.isEmpty()
                    ? getEmptyCoursePageImplementation(courseImplementationId)
                    : coursePageCourseImplementationList.get(0);
            }
        } catch (CoursePageIntegrationException e) {
            // Already logged in getCoursePageData()
        }

        return new CoursePageCourseImplementation();

    }

    @Override
    public List<CoursePageCourseImplementation> getCoursePages(List<String> courseImplementationIds, Locale locale) {
        return Lists.partition(courseImplementationIds, COURSE_IMPLEMENTATION_BATCH_SIZE).parallelStream()
            .map(idListPartition ->
                getCoursePageData("/course_implementation/{courseImplementationIds}",
                    new ParameterizedTypeReference<List<CoursePageCourseImplementation>>() {
                    },
                    locale,
                    String.join(",", idListPartition)))
            .flatMap(Collection::stream)
            .collect(toList());
    }

    @Override
    public List<Long> getUpdatedCourseImplementationIds(long timestamp) {
        List<Long> implementationIds = getCoursePageData(
            "/course_implementation/changes/since/{timestamp}",
            new ParameterizedTypeReference<List<Long>>() {
            }, timestamp);

        if (implementationIds != null) {
            return implementationIds;
        } else {
            return new ArrayList<>();
        }
    }

    private CoursePageCourseImplementation getEmptyCoursePageImplementation(String courseImplementationId) {
        CoursePageCourseImplementation emptyCourseContent = new CoursePageCourseImplementation();
        emptyCourseContent.courseImplementationId = Integer.parseInt(courseImplementationId);
        return emptyCourseContent;
    }

}
