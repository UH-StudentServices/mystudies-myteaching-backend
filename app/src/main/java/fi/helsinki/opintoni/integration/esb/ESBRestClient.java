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

package fi.helsinki.opintoni.integration.esb;

import fi.helsinki.opintoni.integration.oodi.OodiRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

public class ESBRestClient implements ESBClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OodiRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ESBRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber) {
        try {
            return restTemplate.exchange(
                "{baseUrl}/person/v2/employee/{employeeNumber}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ESBEmployeeInfo>>() {
                },
                baseUrl,
                employeeNumber).getBody();
        } catch (Exception e) {
            LOGGER.error("Error when fetching employee info from ESB", e);
            return newArrayList();
        }
    }

    @Override
    public Optional<OptimeStaffInformation> getStaffInformation(String staffId) {
        try {
            return
                Optional.ofNullable(restTemplate.exchange(
                    "{baseUrl}/optime/staff/{staffId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<OptimeStaffInformation>() {
                    },
                    baseUrl, staffId).getBody());
        } catch (Exception e) {
            LOGGER.error("Error when fetching Optime staff information info from ESB", e);
            return Optional.empty();
        }
    }
}
