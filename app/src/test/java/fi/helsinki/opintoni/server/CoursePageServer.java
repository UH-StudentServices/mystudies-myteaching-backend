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
import fi.helsinki.opintoni.service.UserNotificationServiceTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

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

    public void expectCourseImplementationRequest(String courseImplementationId) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("coursepage/courses.json"), MediaType.APPLICATION_JSON));
    }

    public void expectCourseImplementationRequest(String courseImplementationId, String responseFile) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectStudentCourseImplementationEventsRequest(String courseImplementationId) {
        server.expect(requestTo(eventsUrl(courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/studentevents.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectTeacherCourseImplementationEventsRequest(String courseImplementationId) {
        server.expect(requestTo(eventsUrl(courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/teacherevents.json"),
                    MediaType.APPLICATION_JSON)
            );
    }


    public void expectCourseImplementationActivityRequest(String courseImplementationId, String responseFile) {
        server.expect(requestTo(new UserNotificationServiceTest.ActivityUrlMatcher(coursePageBaseUrl, courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    private String courseImplementationUrl(String courseImplementationId) {
        return coursePageBaseUrl + "/course_implementations?course_implementation_id=" +
            courseImplementationId;
    }

    private String eventsUrl(String courseImplementationId) {
        return coursePageBaseUrl + "/events?course_implementation_id=" + courseImplementationId;
    }
}
