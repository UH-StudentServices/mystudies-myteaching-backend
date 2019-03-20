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

package fi.helsinki.opintoni.server;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.unisport.MockUnisportJWTService;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static fi.helsinki.opintoni.sampledata.SampleDataFiles.toText;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class UnisportServer extends AbstractRestServiceServer {

    private final String unisportBaseUrl;

    public UnisportServer(AppConfiguration appConfiguration,
                          RestTemplate unisportRestTemplate) {
        super(MockRestServiceServer.createServer(unisportRestTemplate));
        this.unisportBaseUrl = appConfiguration.get("unisport.base.url");
    }

    public void expectAuthorization() {
        server.expect(requestTo(unisportBaseUrl + "/v1/en/ext/opintoni/authorization?eppn=opiskelija@helsinki.fi"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    toText("unisport/user.json"),
                    MediaType.APPLICATION_JSON
                )
            );
    }

    public void expectAuthorizationFailWith404() {
        server.expect(requestTo(unisportBaseUrl + "/v1/en/ext/opintoni/authorization?eppn=opettaja@helsinki.fi"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withStatus(HttpStatus.NOT_FOUND)
            );
    }

    public void expectUserReservations() {
        server.expect(requestTo(unisportBaseUrl + "/v1/fi/ext/opintoni/reservations"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", MockUnisportJWTService.MOCK_JWT_TOKEN))
            .andRespond(
                withSuccess(
                    toText("unisport/user-reservations.json"),
                    MediaType.APPLICATION_JSON
                )
            );
    }
}
