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

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.sotka.model.SotkaHierarchy;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class SotkaRestClient implements SotkaClient {

    private static final Logger log = LoggerFactory.getLogger(SotkaRestClient.class);
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public SotkaRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = CacheConstants.SOTKA, cacheManager = "transientCacheManager")
    public Optional<SotkaHierarchy> getOptimeHierarchy(String optimeId) {
        return getSotkaData(baseUrl + "/oodi/hierarchy/optime/{realisationId}",
            new ParameterizedTypeReference<SotkaHierarchy>() {
            }, optimeId);
    }

    @Override
    @Cacheable(value = CacheConstants.SOTKA, cacheManager = "transientCacheManager")
    public List<SotkaHierarchy> getOptimeHierarchy(List<String> optimeIds) {
        // TODO: remove throw and uncomment implementation once Sotka side is working
        throw new NotImplementedException("POST /oodi/hierarchy/optime is not implemented at Sotka side");
        /*return postSotkaData(baseUrl + "/oodi/hierarchy/optime",
            new ParameterizedTypeReference<List<SotkaHierarchy>>() {
            }, optimeIds)
            .orElse(Collections.emptyList());*/
    }

    private <T> Optional<T> getSotkaData(String url, ParameterizedTypeReference<T> typeReference, Object... uriVariables) {
        try {
            return Optional.ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, typeReference, uriVariables).getBody());
        } catch (Exception e) {
            log.error("Caught Sotka integration exception", e);
            throw new SotkaIntegrationException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
    private <T> Optional<T> postSotkaData(String url, ParameterizedTypeReference<T> typeReference, Object body) {
        try {
            RequestEntity<Object> request = RequestEntity
                .post(new URI(url))
                .accept(MediaType.APPLICATION_JSON)
                .body(body);

            return Optional.ofNullable(restTemplate.exchange(request, typeReference).getBody());
        } catch (Exception e) {
            log.error("Caught Sotka integration exception", e);
            throw new SotkaIntegrationException(e.getMessage(), e);
        }
    }

}
