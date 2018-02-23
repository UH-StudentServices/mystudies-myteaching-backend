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

package fi.helsinki.opintoni.integration.sisu;

import fi.helsinki.opintoni.cache.CacheConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SisuRestClient implements SisuClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public SisuRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_ENROLMENTS, cacheManager = "transientCacheManager")
    public List<Enrolment> getEnrolments(String personId) {
        return getSisuData(
            String.format("ilmo/api/enrolments/v1/for-person?personId=%s", personId),
            new ParameterizedTypeReference<List<Enrolment>>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_COURSE_UNIT_REALISATION, cacheManager = "transientCacheManager")
    public CourseUnitRealisation getCourseUnitRealisation(String courseUnitRealisationId) {
        return getSisuData(
            String.format("kori/api/course-unit-realisations/v1/%s", courseUnitRealisationId),
            new ParameterizedTypeReference<CourseUnitRealisation>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_ATTAINMENTS, cacheManager = "transientCacheManager")
    public List<Attainment> getAttainments(String personId) {
        return getSisuData(
            String.format("ori/api/attainments/v1/for-person?personId=%s", personId),
            new ParameterizedTypeReference<List<Attainment>>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_COURSE_UNIT, cacheManager = "transientCacheManager")
    public List<CourseUnit> getCourseUnits(String courseUnitGroupId) {
        return getSisuData(
            String.format("kori/api/course-units/v1?groupId=%s", courseUnitGroupId),
            new ParameterizedTypeReference<List<CourseUnit>>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_GRADE_SCALE, cacheManager = "transientCacheManager")
    public GradeScale getGradeScale(String gradeScaleId) {
        return getSisuData(
            String.format("kori/api/grade-scales/%s", gradeScaleId),
            new ParameterizedTypeReference<GradeScale>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_ASSESSMENT, cacheManager = "transientCacheManager")
    public Assessment getAssessment(String assessmentId) {
        return getSisuData(
            String.format("/kori/api/assessment-items/v1/%s", assessmentId),
            new ParameterizedTypeReference<Assessment>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_LOCATION, cacheManager = "transientCacheManager")
    public Location getLocation(String locationId) {
        return getSisuData(
            String.format("/kori/api/locations/v1/%s", locationId),
            new ParameterizedTypeReference<Location>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_PERSON, cacheManager = "transientCacheManager")
    public PublicPerson getPerson(String personId) {
        return getSisuData(
            String.format("/kori/api/persons/v1/%s", personId),
            new ParameterizedTypeReference<PublicPerson>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_BUILDING, cacheManager = "transientCacheManager")
    public Building getBuilding(String buildingId) {
        return getSisuData(
            String.format("/kori/api/buildings/v1/%s", buildingId),
            new ParameterizedTypeReference<Building>() {});
    }

    @Override
    @Cacheable(value = CacheConstants.SISU_STUDY_EVENT, cacheManager = "transientCacheManager")
    public StudyEvent getStudyEvent(String studyEventId) {
        return getSisuData(
            String.format("/kori/api/study-events/v1/%s", studyEventId), //Check url
            new ParameterizedTypeReference<StudyEvent>() {});
    }

    public <T> T getSisuData(String path, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(
            String.join("/", baseUrl, path),
            HttpMethod.GET,
            null,
            typeReference).getBody();
    }



}
