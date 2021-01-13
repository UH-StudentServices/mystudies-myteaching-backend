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

package fi.helsinki.opintoni.integration.studies;

import fi.helsinki.opintoni.util.FunctionHelper;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudiesRestClient implements StudiesClient {
    private static final Logger logger = LoggerFactory.getLogger(StudiesRestClient.class);

    private static final int COURSE_IDS_LIST_PARTITION_SIZE = 100;

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    public StudiesRestClient(RestTemplate restTemplate, String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public Map<String, String> getCoursePageUrls(final List<String> courseIds, Locale locale) {
        if (courseIds != null && !courseIds.isEmpty()) {
            final ParameterizedTypeReference<Map<String, String>> typeReference = new ParameterizedTypeReference<Map<String, String>>() {
            };
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add("languageCode", locale.getLanguage());

            return ListUtils.partition(courseIds, COURSE_IDS_LIST_PARTITION_SIZE).stream()
                .flatMap(FunctionHelper.logAndIgnoreExceptions(courseIdsPartition -> {
                    queryParams.put("courseId", courseIdsPartition);
                    return getStudiesData("/courses/url", typeReference, queryParams).stream()
                        .map(Map::entrySet)
                        .flatMap(Collection::stream);
                }))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Collections.emptyMap();
    }

    private <T> Optional<T> getStudiesData(String path,
                                           ParameterizedTypeReference<T> typeReference,
                                           MultiValueMap<String, String> queryParams,
                                           Object... uriVariables) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl).path(path);
            if (queryParams != null) {
                builder.queryParams(queryParams);
            }

            return Optional.ofNullable(restTemplate.exchange(builder.build(uriVariables), HttpMethod.GET, null, typeReference).getBody());
        } catch (Exception e) {
            logger.error("Caught exception", e);
            throw new StudiesIntegrationException(e.getMessage(), e);
        }
    }
}
