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
import org.hamcrest.Matcher;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class CoursePageServer {

    private final MockRestServiceServer server;
    private final String coursePageBaseUrl;

    public CoursePageServer(AppConfiguration appConfiguration,
                            RestTemplate coursePageRestTemplate) {
        this.server = MockRestServiceServer.createServer(coursePageRestTemplate);
        this.coursePageBaseUrl = appConfiguration.get("coursePage.base.url");
    }

    public void expectCourseImplementationRequest(String courseImplementationId, Locale locale) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("coursepage/course.json"), MediaType.APPLICATION_JSON));
    }

    public void expectCourseImplementationRequest(String courseImplementationId, String responseFile, Locale locale) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectCourseImplementationChangesRequest() {
        server.expect(requestTo(courseImplementationChangesUrlMatcher()))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                SampleDataFiles.toText("coursepage/course_implementation_changes.json"),
                MediaType.APPLICATION_JSON
            ));
    }

    private String courseImplementationUrl(String courseImplementationId, Locale locale) {
        return String.format("%s/%s/course_implementation/%s", coursePageBaseUrl, locale.toString(), courseImplementationId);
    }

    private Matcher<String> courseImplementationChangesUrlMatcher() {
        return startsWith(coursePageBaseUrl + "/course_implementation/changes/since/");
    }
}
