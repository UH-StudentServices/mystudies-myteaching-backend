package fi.helsinki.opintoni.integration.unisport;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Optional;

public class UnisportRestClient implements UnisportClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public UnisportRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
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
                null,
                new ParameterizedTypeReference<UnisportUserReservations>() {
                },
                baseUrl, locale.getLanguage()).getBody();
    }
}
