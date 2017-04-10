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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class LeikiServer {

    private final MockRestServiceServer server;
    private final String leikiBaseUrl;

    public LeikiServer(AppConfiguration appConfiguration,
                       RestTemplate leikiRestTemplate) {
        this.server = MockRestServiceServer.createServer(leikiRestTemplate);
        this.leikiBaseUrl = appConfiguration.get("leiki.base.url");
    }

    public void expectSearchResults(String searchTerm, String responseFile) {
        expextResults(searchUrl(searchTerm), responseFile);
    }

    public void expectCategoryResults(String searchTerm, String responseFile) {
        expextResults(searchCategoryUrl(searchTerm), responseFile);
    }

    public void expectCourseRecommendationsResult(String studentNumber, String responseFile) {
        expextResults(courseRecommendationsUrl(studentNumber), responseFile);
    }

    public void expectCourseRecommendationsErrorResult(String studentNumber) {
        server.expect(requestTo(courseRecommendationsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withServerError());
    }

    private void expextResults(URI requestUrl, String responseFile) {
        server.expect(requestTo(requestUrl))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("leiki/" + responseFile), MediaType.APPLICATION_JSON));
    }

    private URI searchCategoryUrl(String searchTerm) {
        URI uri = UriComponentsBuilder.fromHttpUrl(leikiBaseUrl)
            .path("/focus/api")
            .queryParam("method", "searchcategories")
            .queryParam("autocomplete", "occurred")
            .queryParam("lang", "en")
            .queryParam("text", searchTerm)
            .queryParam("format", "json")
            .queryParam("max", "10")
            .build()
            .encode()
            .toUri();

        return uri;
    }

    private URI searchUrl(String searchTerm) {
        URI uri = UriComponentsBuilder.fromHttpUrl(leikiBaseUrl)
            .path("/focus/api")
            .queryParam("method", "searchc")
            .queryParam("sourceallmatch")
            .queryParam("instancesearch")
            .queryParam("lang", "en")
            .queryParam("query", searchTerm)
            .queryParam("format", "json")
            .queryParam("t_type", "kaikki_en")
            .queryParam("max", "100")
            .queryParam("fulltext", "true")
            .queryParam("partialsearchpriority", "ontology")
            .build()
            .encode()
            .toUri();

        return uri;
    }

    private URI courseRecommendationsUrl(String studentNumber) {
        ZonedDateTime today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);

        URI uri = UriComponentsBuilder.fromHttpUrl(leikiBaseUrl)
            .path("/focus/api")
            .queryParam("method", "getsocial")
            .queryParam("similaritylimit", "99")
            .queryParam("format", "json")
            .queryParam("t_type", "kurssit")
            .queryParam("max", "20")
            .queryParam("uid", "opintohistoriatesti_" + studentNumber)
            .queryParam("showtags", "true")
            .queryParam("unreadonly", "true")
            .queryParam("startdate", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")))
            .queryParam("enddate", "2222-12-31T00:00:00+0300")
            .build()
            .encode()
            .toUri();

        return uri;
    }

}
