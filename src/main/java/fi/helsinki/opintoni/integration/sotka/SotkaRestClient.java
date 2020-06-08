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

package fi.helsinki.opintoni.integration.sotka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class SotkaRestClient implements SotkaClient {

    private static final Logger logger = LoggerFactory.getLogger(SotkaRestClient.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public SotkaRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public OodiHierarchy getOodiHierarchy(String oodiRealisationId) {
        return getSotkaData(baseUrl + "/oodi/hierarchy/{realisationId}",
            new ParameterizedTypeReference<OodiHierarchy>() {
            },
            oodiRealisationId)
            .orElse(new OodiHierarchy());
    }

    private <T> Optional<T> getSotkaData(String url, ParameterizedTypeReference<T> typeReference, Object... uriVariables) {
        try {
            return Optional.ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody());
        } catch (Exception e) {
            logger.error("Caught Sotka integration exception", e);
            throw new SotkaIntegrationException(e.getMessage(), e);
        }
    }

}
