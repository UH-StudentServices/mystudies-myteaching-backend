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
import fi.helsinki.opintoni.service.TimeService;
import org.hamcrest.Matcher;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class CoursePageServer extends AbstractRestServiceServer {

    private final String coursePageBaseUrl;
    private final String coursePageApiPath;

    public CoursePageServer(AppConfiguration appConfiguration,
                            RestTemplate coursePageRestTemplate) {
        super(MockRestServiceServer.createServer(coursePageRestTemplate));

        this.coursePageBaseUrl = appConfiguration.get("coursePage.base.url");
        this.coursePageApiPath = appConfiguration.get("coursePage.api.path");
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

    public void expectCourseImplementationChangesRequest(LocalDateTime sinceDate) {
        expectCourseImplementationChangesRequest(sinceDate, "coursepage/course_implementation_changes.json");
    }

    public void expectCourseImplementationChangesRequestWhenNoChanges(LocalDateTime sinceDate) {
        expectCourseImplementationChangesRequest(sinceDate, "coursepage/course_implementation_changes_no_changes.json");
    }

    private void expectCourseImplementationChangesRequest(LocalDateTime sinceDate, String responseFile) {
        server.expect(requestTo(courseImplementationChangesUrl(sinceDate)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                SampleDataFiles.toText(responseFile),
                MediaType.APPLICATION_JSON
            ));
    }

    private String courseImplementationUrl(String courseImplementationId, Locale locale) {
        return String.format("%s/%s%s/course_implementation/%s",
            coursePageBaseUrl,
            locale.toString(),
            coursePageApiPath,
            courseImplementationId);
    }

    private String courseImplementationChangesUrl(LocalDateTime sinceDate) {
        Long epochSecond = sinceDate.atZone(TimeService.HELSINKI_ZONE_ID).toEpochSecond();
        return String.format("%s%s/course_implementation/changes/since/%s", coursePageBaseUrl, coursePageApiPath, epochSecond);
    }
}
