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

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;

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
    public List<OodiEnrollment> getEnrollments(String studentNumber, Locale locale) {
        return getOodiData("{baseUrl}/students/{studentNumber}/enrollments?langcode={locale}",
            new ParameterizedTypeReference<OodiResponse<OodiEnrollment>>() {
            }, baseUrl, studentNumber, locale.getLanguage());
    }

    @Override
    @Cacheable(CacheConstants.STUDENT_EVENTS)
    public List<OodiEvent> getStudentEvents(String studentNumber, Locale locale) {
        return getOodiData("{baseUrl}/students/{studentNumber}/events?langcode={locale}",
            new ParameterizedTypeReference<OodiResponse<OodiEvent>>() {
            }, baseUrl, studentNumber, locale.getLanguage());
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber, Locale locale) {
        return getOodiData("{baseUrl}/students/{studentNumber}/studyattainments?langcode={locale}",
            new ParameterizedTypeReference<OodiResponse<OodiStudyAttainment>>() {
            }, baseUrl, studentNumber, locale.getLanguage());
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_COURSES)
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, Locale locale, String sinceDateString) {
        return getOodiData("{baseUrl}/teachers/{teacherNumber}/teaching/all?langcode={locale}&since_date={sinceDate}",
            new ParameterizedTypeReference<OodiResponse<OodiTeacherCourse>>() {
            }, baseUrl, teacherNumber, locale.getLanguage() ,sinceDateString);
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber, Locale locale) {
        return getOodiData("{baseUrl}/students/{studentNumber}/studyrights?langcode={locale}",
            new ParameterizedTypeReference<OodiResponse<OodiStudyRight>>() {
            }, baseUrl, studentNumber, locale.getLanguage());
    }

    @Override
    @Cacheable(CacheConstants.COURSE_UNIT_REALISATIONS)
    public OodiCourseUnitRealisation getCourseUnitRealisation(String realisationId, Locale locale) {
        return getSingleOodiData("{baseUrl}/courseunitrealisations/{realisationId}?langcode={locale}",
            new ParameterizedTypeReference<OodiSingleResponse<OodiCourseUnitRealisation>>() {
            }, baseUrl, realisationId, locale.getLanguage());
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return getSingleOodiData("{baseUrl}/students/{studentNumber}/info?langcode=en",
            new ParameterizedTypeReference<OodiSingleResponse<OodiStudentInfo>>() {
            }, baseUrl, studentNumber);
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return getSingleOodiData("{baseUrl}/persons/{oodiPersonId}/roles?langcode=en",
            new ParameterizedTypeReference<OodiSingleResponse<OodiRoles>>() {
            }, baseUrl, oodiPersonId);
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_EVENTS)
    public List<OodiEvent> getTeacherEvents(String teacherNumber, Locale locale) {
        return getOodiData("{baseUrl}/teachers/{teacherNumber}/events?langcode={locale}",
            new ParameterizedTypeReference<OodiResponse<OodiEvent>>() {
            },
            baseUrl, teacherNumber, locale.getLanguage());
    }

    public <T> List<T> getOodiData(String url,
                                   ParameterizedTypeReference<OodiResponse<T>> typeReference,
                                   Object... uriVariables) {
        List<T> data;
        try {
            data = restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody().data;
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
