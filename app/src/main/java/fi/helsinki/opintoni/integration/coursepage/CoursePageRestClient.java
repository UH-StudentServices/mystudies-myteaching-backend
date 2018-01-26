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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoursePageRestClient implements CoursePageClient {
    private final String baseUrl;
    private final String apiPath;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CoursePageRestClient.class);

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

    private String getCoursePageApiUrl(String path, Locale locale) {
        String localeUrlSegment = locale != null ? "/" + locale.getLanguage() : "";

        return baseUrl + localeUrlSegment + apiPath + path;
    }

    public <T> List<T> getCoursePageData(
        String path,
        ParameterizedTypeReference<List<T>> typeReference,
        Locale locale,
        Object... uriVariables) {
        String url = null;

        try {
            url = getCoursePageApiUrl(path, locale);
            return restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody();
        } catch (Exception e) {
            log.error("Caught exception when calling Course Pages URL " + url, e);
            throw new CoursePageIntegrationException(e.getMessage(), e);
        }
    }

    @Override
    @Cacheable(
        value = CacheConstants.COURSE_PAGE,
        key = "#courseImplementationId + '_' + #locale.toString()",
        unless = "#result.courseImplementationId == null",
        cacheManager = "persistentCacheManager")
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId, Locale locale) {
        log.trace("fetching course impl with id {} and locale {}", courseImplementationId, locale.toString());

        try {
            List<CoursePageCourseImplementation> coursePageCourseImplementationList = getCoursePageData(
                    "/course_implementation/{courseImplementationId}",
                    new ParameterizedTypeReference<List<CoursePageCourseImplementation>>() {},
                    locale,
                    courseImplementationId);

            if(coursePageCourseImplementationList != null && coursePageCourseImplementationList.size() > 0) {
                return coursePageCourseImplementationList.get(0);
            }
        } catch (CoursePageIntegrationException e) {
            // CoursePageIntegrationException already logged in getCoursePageData
        }
        return new CoursePageCourseImplementation();
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
