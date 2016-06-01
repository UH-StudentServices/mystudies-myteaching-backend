package fi.helsinki.opintoni.server;


import fi.helsinki.opintoni.config.AppConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static fi.helsinki.opintoni.sampledata.SampleDataFiles.toText;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class UnisportServer {

    private final MockRestServiceServer server;
    private final String unisportBaseUrl;

    public UnisportServer(AppConfiguration appConfiguration,
                        RestTemplate unisportRestTemplate) {
        this.server = MockRestServiceServer.createServer(unisportRestTemplate);
        this.unisportBaseUrl = appConfiguration.get("unisport.base.url");
    }

    public void expectAuthorization() {
        server.expect(requestTo(unisportBaseUrl + "/api/v1/fi/ext/opintoni/authorization?eppn=opiskelija@helsinki.fi"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    toText("unisport/user.json"),
                    MediaType.APPLICATION_JSON
                )
            );
    }

    public void expectUserReservations() {
        server.expect(requestTo(unisportBaseUrl + "/api/v1/fi/ext/opintoni/reservations"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    toText("unisport/user-reservations.json"),
                    MediaType.APPLICATION_JSON
                )
            );
    }
}
