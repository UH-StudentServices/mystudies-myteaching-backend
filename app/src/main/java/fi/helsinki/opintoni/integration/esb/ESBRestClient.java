package fi.helsinki.opintoni.integration.esb;

import fi.helsinki.opintoni.integration.oodi.OodiRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class ESBRestClient implements ESBClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(OodiRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ESBRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber) {
        try {
            return Optional.ofNullable(restTemplate.exchange(
                "{baseUrl}/person/v2/employeeList",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ESBEmployeeInfo>() {
                },
                baseUrl).getBody());
        } catch (Exception e) {
            LOGGER.error("Error when fetching employee info from ESB", e);
            return Optional.empty();
        }
    }
}
