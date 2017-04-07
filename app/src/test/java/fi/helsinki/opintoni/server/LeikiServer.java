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

import java.time.LocalDate;

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

    private void expextResults(String requestUrl, String responseFile) {
        server.expect(requestTo(requestUrl))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("leiki/" + responseFile), MediaType.APPLICATION_JSON));
    }

    private String searchCategoryUrl(String searchTerm) {
        return String.format("%s/focus/api?method=searchcategories&autocomplete=occurred"
            + "&lang=en&text=%s&format=json&max=10", leikiBaseUrl, searchTerm);
    }

    private String searchUrl(String searchTerm) {
        return String.format("%s/focus/api?method=searchc&sourceallmatch&instancesearch"
            + "&lang=en&query=%s&format=json&t_type=kaikki_en&max=100&fulltext=true"
            + "&partialsearchpriority=ontology", leikiBaseUrl, searchTerm);
    }

    private String courseRecommendationsUrl(String studentNumber) {
        LocalDate today = LocalDate.now();

        return String.format("%s/focus/api?method=getsocial&"
            + "similaritylimit=99&format=json&t_type=kurssit&max=20&uid=opintohistoriatesti_%s"
            + "&showtags=true&unreadonly=true&startdate=%sT00:00:00%%2B0300&enddate=2222-12-31T00:00:00%%2B0300",
            leikiBaseUrl, studentNumber, today.toString());
    }

}
