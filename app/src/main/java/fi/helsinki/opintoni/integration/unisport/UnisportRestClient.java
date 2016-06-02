package fi.helsinki.opintoni.integration.unisport;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
    public Optional<UnisportUser> getUnisportUserByPrincipal(String userName, Locale locale) {
        return Optional.ofNullable(
            restTemplate.exchange(
                "{baseUrl}/api/v1/{locale}/ext/opintoni/authorization?eppn={userName}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<UnisportUser>() {
                },
                baseUrl, locale.getLanguage(), userName).getBody());
    }

    @Override
    public UnisportUserReservations getUserReservations(Long unisportUserId, Locale locale) {
        return
            restTemplate.exchange(
                "{baseUrl}/api/v1/{locale}/ext/opintoni/reservations",
                HttpMethod.GET,
                getAuthorizationHeader(unisportUserId),
                new ParameterizedTypeReference<UnisportUserReservations>() {
                },
                baseUrl, locale.getLanguage()).getBody();
    }

    private HttpEntity<String> getAuthorizationHeader(final Long unisportUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, unisportJWTService.generateToken(unisportUserId));
        return new HttpEntity<>("parameters", headers);
    }
}
