package fi.helsinki.opintoni.integration.iam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class IAMRestClient implements IAMClient {

    private static final Logger log = LoggerFactory.getLogger(IAMRestClient.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public IAMRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<AccountStatus> getAccountStatus(String username) {
        String url = baseUrl + "/" + username + "/status";
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, AccountStatus.class));
        } catch (Exception e) {
            log.error("IAM status request failed", e);
            return Optional.empty();
        }
    }

}
