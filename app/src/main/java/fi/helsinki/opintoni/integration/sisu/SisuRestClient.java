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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SisuRestClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public SisuRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public List<Enrolment> getEnrolments(String personId) {
        return getSisuData(
            String.format("ilmo/api/enrolments/v1/for-person?personId=%s", personId),
            new ParameterizedTypeReference<List<Enrolment>>() {});
    }

    public CourseUnitRealisation getCourseUnitRealisation(String courseUnitRealisationId) {
        return getSisuData(
            String.format("kori/api/course-unit-realisations/v1/%s", courseUnitRealisationId),
            new ParameterizedTypeReference<CourseUnitRealisation>() {});
    }

    public Assessment getAssessment(String assessmentId) {
        return getSisuData(
            String.format("/kori/api/assessment-items/v1/%s", assessmentId),
            new ParameterizedTypeReference<Assessment>() {});
    }

    public <T> T getSisuData(String path, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(
            String.join("/", baseUrl, path),
            HttpMethod.GET,
            null,
            typeReference).getBody();
    }



}
