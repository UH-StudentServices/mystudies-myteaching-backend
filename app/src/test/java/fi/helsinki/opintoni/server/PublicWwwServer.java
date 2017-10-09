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
    private final String publicWwwUrl;

    private static final String PUBLIC_WWW_PATH = "/fi/feeds/filtered-news/rss/11405/all";
    private static final String OPEN_UNI_NEWS_FILE = "newsfeeds/flamma/studentopenuniversitynews.xml";

    public PublicWwwServer(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.publicWwwUrl = appConfiguration.get("publicWww.base.url") + PUBLIC_WWW_PATH;
    }

    public void expectStudentOpenUniversityNews() {
        server.expect(requestTo(publicWwwUrl))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(OPEN_UNI_NEWS_FILE), new MediaType("application", "rss+xml")));
    }

}
