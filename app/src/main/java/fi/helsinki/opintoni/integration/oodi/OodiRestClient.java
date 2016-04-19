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

package fi.helsinki.opintoni.integration.oodi;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

public class OodiRestClient implements OodiClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(OodiRestClient.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public OodiRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(CacheConstants.STUDENT_ENROLLMENTS)
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        return getOodiData("{baseUrl}/students/{studentNumber}/enrollments",
            new ParameterizedTypeReference<OodiResponse<OodiEnrollment>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    @Cacheable(CacheConstants.STUDENT_EVENTS)
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        return getOodiData("{baseUrl}/students/{studentNumber}/events",
            new ParameterizedTypeReference<OodiResponse<OodiEvent>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        return getOodiData("{baseUrl}/students/{studentNumber}/studyattainments",
            new ParameterizedTypeReference<OodiResponse<OodiStudyAttainment>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_COURSES)
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return getOodiData("{baseUrl}/teachers/{teacherNumber}/teaching/all?since_date={sinceDate}",
            new ParameterizedTypeReference<OodiResponse<OodiTeacherCourse>>() {
            }, baseUrl, teacherNumber, sinceDateString);
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber) {
        return getOodiData("{baseUrl}/students/{studentNumber}/studyrights",
            new ParameterizedTypeReference<OodiResponse<OodiStudyRight>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    @Cacheable(CacheConstants.COURSE_UNIT_REALISATIONS)
    public OodiCourseUnitRealisation getCourseUnitRealisation(String realisationId) {
        return getSingleOodiData("{baseUrl}/courseunitrealisations/{realisationId}",
            new ParameterizedTypeReference<OodiSingleResponse<OodiCourseUnitRealisation>>() {
            }, baseUrl, realisationId);
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return getSingleOodiData("{baseUrl}/students/{studentNumber}/info",
            new ParameterizedTypeReference<OodiSingleResponse<OodiStudentInfo>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return getSingleOodiData("{baseUrl}/persons/{oodiPersonId}/roles",
            new ParameterizedTypeReference<OodiSingleResponse<OodiRoles>>() {
            }, baseUrl, oodiPersonId);
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_EVENTS)
    public List<OodiEvent> getTeacherEvents(String teacherNumber) {
        return getOodiData("{baseUrl}/teachers/{teacherNumber}/events",
            new ParameterizedTypeReference<OodiResponse<OodiEvent>>() {
            },
            baseUrl, teacherNumber);
    }

    public <T> List<T> getOodiData(String url,
                                   ParameterizedTypeReference<OodiResponse<T>> typeReference,
                                   Object... uriVariables) {
        List<T> data;
        try {
            data = Optional
                .ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody())
                .map(r -> r.data)
                .orElse(Lists.newArrayList());

        } catch (Exception e) {
            LOGGER.error("Caught OodiIntegrationException", e);
            throw new OodiIntegrationException(e.getMessage(), e);
        }
        return data;
    }

    public <T> T getSingleOodiData(String url, ParameterizedTypeReference<OodiSingleResponse<T>> typeReference,
                                   Object... uriVariables) {
        T data;
        try {
            data = restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody().data;
        } catch (Exception e) {
            LOGGER.error("Caught OodiIntegrationException", e);
            throw new OodiIntegrationException(e.getMessage(), e);
        }
        return data;
    }
}
