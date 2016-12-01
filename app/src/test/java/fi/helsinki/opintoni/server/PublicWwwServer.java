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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static fi.helsinki.opintoni.sampledata.SampleDataFiles.toText;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class PublicWwwServer {

    private final MockRestServiceServer server;
    private final String publicWwwBaseUrl;

    public PublicWwwServer(AppConfiguration appConfiguration,
                           RestTemplate publicWwwRestTemplate) {
        this.server = MockRestServiceServer.createServer(publicWwwRestTemplate);
        this.publicWwwBaseUrl = appConfiguration.get("publicWww.base.url");
    }

    public void expectStudentOpenUniversityNews() {
        server.expect(requestTo(publicWwwBaseUrl + "/fi/feeds/filtered-news/rss/11405/all"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText("flamma/studentopenuniversitynews.xml"), MediaType.TEXT_XML));
    }

    public void expectEnglishStudentOpenUniversityNews() {
        server.expect(requestTo(publicWwwBaseUrl + "/fi/feeds/filtered-news/rss/11405/all"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText("flamma/englishstudentopenuniversitynews.xml"), MediaType.TEXT_XML));
    }

}
