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

package fi.helsinki.opintoni.integration.optime;

import fi.helsinki.opintoni.integration.coursepage.CoursePageRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

public class OptimeRestClient implements OptimeClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(OptimeRestClient.class);

    public OptimeRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public OptimeStaffInformation getStaffInformation(String staffId) {
        log.trace("fetching Optime information with id {}", staffId);

        return
            restTemplate.exchange(
                "{baseUrl}/optime/staff/{staffId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OptimeStaffInformation>() {
                },
                baseUrl, staffId).getBody();
    }
}
