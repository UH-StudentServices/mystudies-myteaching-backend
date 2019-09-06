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

import fi.helsinki.opintoni.sampledata.OpenGraphSampleData;
import fi.helsinki.opintoni.sampledata.RSSFeedSampleData;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class WebPageServer extends AbstractRestServiceServer {

    public WebPageServer(RestTemplate metaDataRestTemplate) {
        super(MockRestServiceServer.createServer(metaDataRestTemplate));
    }

    public void expectMetaDataRequest() {
        server.expect(requestTo(OpenGraphSampleData.URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("pagemetadata/document.html"), MediaType.TEXT_HTML));
    }

    public void expectRssFeedRequest(String firstFeedUrl, String secondFeedUrl) {
        server.expect(requestTo(RSSFeedSampleData.WEBPAGE_CONTAINING_RSS_FEED_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                String.format(SampleDataFiles.toText("rssfeedwebpage/rssFeedWebPage.html"), firstFeedUrl, secondFeedUrl), MediaType.TEXT_HTML));
    }

    public void expectMetaDataNotFound() {
        server.expect(requestTo(OpenGraphSampleData.INVALID_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
    }

}
