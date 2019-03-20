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

package fi.helsinki.opintoni.integration.unisport;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Optional;

public class UnisportRestClient implements UnisportClient {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final UnisportJWTService unisportJWTService;

    public UnisportRestClient(String baseUrl, RestTemplate restTemplate, UnisportJWTService unisportJWTService) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.unisportJWTService = unisportJWTService;
    }

    @Override
    public Optional<UnisportUser> getUnisportUserByPrincipal(String username) {
        UnisportUser unisportUser = null;
        try {
            unisportUser = restTemplate.exchange(
                baseUrl + "/v1/{locale}/ext/opintoni/authorization?eppn={userName}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<UnisportUser>() {
                }, new Locale("en"), username).getBody();

        } catch (HttpStatusCodeException e) {
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw e;
            }
        }
        return Optional.ofNullable(unisportUser);
    }

    @Override
    public UnisportUserReservations getUserReservations(Long unisportUserId, Locale locale) {
        return
            restTemplate.exchange(
                baseUrl + "/v1/{locale}/ext/opintoni/reservations",
                HttpMethod.GET,
                getAuthorizationHeader(unisportUserId),
                new ParameterizedTypeReference<UnisportUserReservations>() {
                }, locale.getLanguage()).getBody();
    }

    private HttpEntity<String> getAuthorizationHeader(final Long unisportUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, unisportJWTService.generateToken(unisportUserId));
        return new HttpEntity<>("parameters", headers);
    }
}
