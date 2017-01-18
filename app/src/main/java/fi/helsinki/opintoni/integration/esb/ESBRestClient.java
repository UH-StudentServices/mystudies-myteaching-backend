package fi.helsinki.opintoni.integration.esb;

import fi.helsinki.opintoni.integration.oodi.OodiRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ESBRestClient implements ESBClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(OodiRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ESBRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber) {
        try {
            return restTemplate.exchange(
                "{baseUrl}/person/v2/employee/{employeeNumber}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ESBEmployeeInfo>>() {
                },
                baseUrl,
                employeeNumber).getBody();
        } catch (Exception e) {
            LOGGER.error("Error when fetching employee info from ESB", e);
            return newArrayList();
        }
    }

    @Override
    public OptimeStaffInformation getStaffInformation(String staffId) {
        LOGGER.trace("fetching Optime information with id {}", staffId);

        return
            restTemplate.exchange(
                "{baseUrl}/optime/staff/{staffId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OptimeStaffInformation>() {
                },
                baseUrl, staffId).getBody();
    }
}
