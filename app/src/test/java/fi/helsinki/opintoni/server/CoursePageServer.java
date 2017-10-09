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
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    public void expectCourseImplementationRequest(String courseImplementationId) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("coursepage/course.json"), MediaType.APPLICATION_JSON));
    }

    public void expectCourseImplementationRequest(String courseImplementationId, String responseFile) {
        server.expect(requestTo(courseImplementationUrl(courseImplementationId)))
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

    public void expectCourseImplementationActivityRequest(List<String> courseImplementationIds, String responseFile) {
        server.expect(
            requestTo(new ActivityUrlMatcher(coursePageBaseUrl, courseImplementationIdsToString(courseImplementationIds))))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("coursepage/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    private String courseImplementationUrl(String courseImplementationId) {
        return coursePageBaseUrl + "/course_implementation/" +
            courseImplementationId;
    }

    private Matcher<String> courseImplementationChangesUrlMatcher() {
        return startsWith(coursePageBaseUrl + "/course_implementation/changes/since/");
    }

    private String eventsUrl(String courseImplementationId) {
        return coursePageBaseUrl + "/events?course_implementation_id=" + courseImplementationId;
    }

    private String courseImplementationIdsToString(List<String> courseImplementationIds) {
        StringBuilder builder = new StringBuilder();
        builder.append(courseImplementationIds.get(0));
        courseImplementationIds.stream()
            .skip(1)
            .forEach(i -> builder.append(",").append(i));
        return builder.toString();
    }

    public static class ActivityUrlMatcher extends TypeSafeMatcher<String> {

        private final String courseRealisationId;
        private final String urlTemplate;

        public ActivityUrlMatcher(String coursePageBaseUrl, String courseRealisationId) {
            this.courseRealisationId = courseRealisationId;
            this.urlTemplate = coursePageBaseUrl + "/course_implementation_activity" +
                "?course_implementation_id=%s&timestamp=";
        }

        @Override
        public boolean matchesSafely(String url) {
            return StringUtils.contains(
                url,
                String.format(urlTemplate, courseRealisationId));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("url containing " + urlTemplate, courseRealisationId));
        }
    }
}
