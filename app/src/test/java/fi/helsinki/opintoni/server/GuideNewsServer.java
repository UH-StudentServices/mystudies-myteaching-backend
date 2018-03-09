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

public class GuideNewsServer extends AbstractRestServiceServer {

    private final String guideNewsPathFi;
    private static final String GUIDE_NEWS_FILE_FI    = "newsfeeds/guide/feed.xml";

    private final String guideNewsPathEn;
    private static final String GUIDE_NEWS_FILE_EN    = "newsfeeds/guide/feed-en.xml";

    public GuideNewsServer(AppConfiguration appConfiguration, RestTemplate guideRestTemplate) {
        super(MockRestServiceServer.createServer(guideRestTemplate));

        this.guideNewsPathFi = appConfiguration.get("newsfeeds.guideFeedsByLocale.fi");
        this.guideNewsPathEn = appConfiguration.get("newsfeeds.guideFeedsByLocale.en");
    }

    public void expectGuideNewsFi() {
        server.expect(requestTo(guideNewsPathFi))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(GUIDE_NEWS_FILE_FI), MediaType.TEXT_XML));
    }

    public void expectGuideNewsEn() {
        server.expect(requestTo(guideNewsPathEn))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(GUIDE_NEWS_FILE_EN), MediaType.TEXT_XML));
    }

    public void expectGuideProgrammeNewsFi(String programmeDegreeCode, String responseFile) {
        server.expect(requestTo(guideNewsPathFi + "?degree_programme_code=" + programmeDegreeCode))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText("newsfeeds/guide/" + responseFile), MediaType.TEXT_XML));
    }
}
