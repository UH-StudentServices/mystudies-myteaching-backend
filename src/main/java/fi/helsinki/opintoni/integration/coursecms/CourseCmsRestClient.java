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

package fi.helsinki.opintoni.integration.coursecms;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.IntegrationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.integration.IntegrationUtil.getSisuCourseUnitRealisationId;

public class CourseCmsRestClient implements CourseCmsClient {

    private static final Logger logger = LoggerFactory.getLogger(CourseCmsRestClient.class);

    private static final List<String> ATTRIBUTES = List.of(
        "field_course_unit_realisation_id",
        "field_image",
        "field_moodle_link",
        "field_introduction_title");

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public CourseCmsRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(
        value = CacheConstants.COURSE_CMS,
        key = "#curId + '_' + #locale.getLanguage()",
        unless = "#result.courseUnitRealisationId == null",
        cacheManager = "transientCacheManager"
    )
    public CourseCmsCourseUnitRealisation getCoursePage(String curId, Locale locale) {
        return getCourseCmsData(Collections.singletonList(curId), locale).stream()
            .findFirst()
            .orElseGet(() -> {
                CourseCmsCourseUnitRealisation emptyRealisation = new CourseCmsCourseUnitRealisation();
                emptyRealisation.courseUnitRealisationId = getSisuCourseUnitRealisationId(curId);
                return emptyRealisation;
            });
    }

    @Override
    public List<CourseCmsCourseUnitRealisation> getCoursePages(List<String> curIds, Locale locale) {
        return getCourseCmsData(curIds, locale);
    }

    private List<CourseCmsCourseUnitRealisation> getCourseCmsData(List<String> curIds, Locale locale) {
        String url = buildCmsDataUri(curIds, locale);
        CourseCmsResponseWrapper response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<CourseCmsResponseWrapper>() {
            }).getBody();
        } catch (Exception e) {
            logger.error("Caught error while fetching data from course cms with URL {}", url, e);
            throw new CourseCmsIntegrationException(e.getMessage(), e);
        }

        if (response != null && response.errors != null && response.errors.size() > 0) {
            logger.error("Received errors from course cms with url {}, {}", url, response.errors);
        }

        if (response != null && response.data != null) {
            return response.data;
        }

        return Collections.emptyList();
    }

    private String getCmsJsonApiUrl(Locale locale) {
        return String.format("%s/%s/jsonapi/node/course", baseUrl, locale != null ? locale.getLanguage() : "fi");
    }

    private String getFilterString(List<String> curIds) {
        String idFilterValuesString = curIds.stream()
            .map(IntegrationUtil::getSisuCourseUnitRealisationId)
            .map(curId -> "&filter[cur-id][condition][value][]=" + curId)
            .collect(Collectors.joining());

        return "filter[cur-id][condition][path]=field_course_unit_realisation_id&filter[cur-id][condition][operator]=IN" + idFilterValuesString;
    }

    private String buildCmsDataUri(List<String> curIds, Locale locale) {
        return getCmsJsonApiUrl(locale) +
            "?" + getFilterString(curIds) +
            "&fields[node--course]=" + String.join(",", ATTRIBUTES) +
            "&include=field_image" +
            "&jsonapi_include=1";
    }
}
