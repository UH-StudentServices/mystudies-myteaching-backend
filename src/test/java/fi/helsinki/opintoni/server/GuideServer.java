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
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class GuideServer extends AbstractRestServiceServer {

    private final String guideBaseUrl;
    private final String degreeProgrammesUrl;

    public GuideServer(AppConfiguration appConfiguration, RestTemplate guideRestTemplate) {
        super(MockRestServiceServer.createServer(guideRestTemplate));
        this.guideBaseUrl = appConfiguration.get("guide.base.url");
        this.degreeProgrammesUrl = this.guideBaseUrl + "/degree-programme?_format=json";
    }

    public void expectDegreeProgrammesRequest() {
        server.expect(requestTo(degreeProgrammesUrl))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(SampleDataFiles.toText("guide/degree-programmes.json"), MediaType.APPLICATION_JSON));
    }

}
