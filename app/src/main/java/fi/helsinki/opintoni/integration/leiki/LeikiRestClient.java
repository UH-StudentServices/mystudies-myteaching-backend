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

package fi.helsinki.opintoni.integration.leiki;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class LeikiRestClient implements LeikiClient {

    private final static Logger log = LoggerFactory.getLogger(LeikiRestClient.class);

    private final String baseUrl;
    private final int maxSearchResults;
    private final int maxCategoryResults;
    private final int maxRecommendations;
    private final String recommendationUidPrefix;
    private final RestTemplate restTemplate;

    public LeikiRestClient(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = appConfiguration.get("leiki.base.url");
        this.maxSearchResults = appConfiguration.getInteger("search.maxSearchResults");
        this.maxCategoryResults = appConfiguration.getInteger("search.maxCategoryResults");
        this.maxRecommendations = appConfiguration.getInteger("recommendations.maxRecommendations");
        this.recommendationUidPrefix = appConfiguration.get("recommendations.uidPrefix");
    }

    @Override
    public List<LeikiSearchHit> search(String searchTerm, Locale locale) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/focus/api")
            .queryParam("method", "searchc")
            .query("sourceallmatch")
            .query("instancesearch")
            .queryParam("lang", locale.getLanguage())
            .queryParam("query", searchTerm)
            .queryParam("format", "json")
            .queryParam("t_type", LeikiTType.getByLocale(locale).getValue())
            .queryParam("max", maxSearchResults)
            .queryParam("fulltext", "true")
            .queryParam("partialsearchpriority", "ontology").build().encode().toUri();

        return getLeikiItemsData(uri, new ParameterizedTypeReference<LeikiResponse<LeikiSearchHit>>() {});
    }

    @Override
    public List<LeikiCategoryHit> searchCategory(String searchTerm, Locale locale) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/focus/api")
            .queryParam("method", "searchcategories")
            .queryParam("autocomplete", "occurred")
            .queryParam("lang", locale.getLanguage())
            .queryParam("text", searchTerm)
            .queryParam("format", "json")
            .queryParam("max", maxCategoryResults).build().encode().toUri();

        return getLeikiCategoryData(uri, new ParameterizedTypeReference<LeikiCategoryResponse<LeikiCategoryHit>>() {});
    }

    @Override
    public List<LeikiCourseRecommendation> getCourseRecommendations(String studentNumber) {
        ZonedDateTime today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/focus/api")
            .queryParam("method", "getsocial")
            .queryParam("similaritylimit", "99")
            .queryParam("format", "json")
            .queryParam("t_type", "kurssit")
            .queryParam("max", maxRecommendations)
            .queryParam("uid", recommendationUidPrefix + studentNumber)
            .queryParam("showtags", "true")
            .queryParam("unreadonly", "true")
            .queryParam("startdate", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")))
            .queryParam("enddate", "2222-12-31T00:00:00+0300")
            .build()
            .encode()
            .toUri();

        return getLeikiItemsData(uri, new ParameterizedTypeReference<LeikiResponse<LeikiCourseRecommendation>>() {});
    }

    private <T> List<T> getLeikiItemsData(URI uri,
                                          ParameterizedTypeReference<LeikiResponse<T>> typeReference) {
        try {
            LeikiData<T> leikiData = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                typeReference).getBody().data;

            return leikiData.items != null ? leikiData.items : newArrayList();
        } catch (RestClientException e) {
            log.error("Leiki client threw exception: {}", e.getMessage());

            return newArrayList();
        }
    }

    private <T> List<T> getLeikiCategoryData(URI uri,
                                             ParameterizedTypeReference<LeikiCategoryResponse<T>> typeReference) {
        try {
            LeikiCategoryData<T> leikiCategoryData = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                typeReference).getBody().data;

            return isNotEmpty(leikiCategoryData.matches) ?
                leikiCategoryData.matches.get(0).match :
                newArrayList();
        } catch (RestClientException e) {
            log.error("Leiki client threw exception: {}", e.getMessage());

            return newArrayList();
        }
    }
}
