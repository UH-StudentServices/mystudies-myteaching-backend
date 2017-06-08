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

package fi.helsinki.opintoni.integration.guide;

import fi.helsinki.opintoni.cache.CacheConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

public class GuideRestClient implements GuideClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(GuideRestClient.class);

    public GuideRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Cacheable(CacheConstants.GUIDE_DEGREE_PROGRAMMES)
    @Override
    public List<GuideDegreeProgramme> getDegreeProgrammes() {
        try {
            ResponseEntity<List<GuideDegreeProgramme>> responseEntity =
                restTemplate.exchange("{baseUrl}/degree-programme?_format=json",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GuideDegreeProgramme>>() {},
                    baseUrl);
            return Optional.ofNullable(responseEntity.getBody())
                .orElse(newArrayList());
        } catch (RestClientException e) {
            log.error("Guide REST client to get Degree Programmes threw exception: {}", e.getMessage());
            return newArrayList();
        }
    }

}
