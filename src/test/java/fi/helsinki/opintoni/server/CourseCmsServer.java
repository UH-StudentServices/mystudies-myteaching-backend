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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static fi.helsinki.opintoni.integration.IntegrationUtil.getSisuCourseUnitRealisationId;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class CourseCmsServer extends AbstractRestServiceServer {

    private static final List<String> ATTRIBUTES = List.of(
        "field_course_unit_realisation_id",
        "field_image",
        "field_moodle_link",
        "field_introduction_title");

    private final String courseCmsBaseUrl;

    public CourseCmsServer(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        super(MockRestServiceServer.createServer(restTemplate));

        this.courseCmsBaseUrl = appConfiguration.get("courseCms.base.url");
    }

    public void expectCourseUnitRealisationRequest(String curId, Locale locale) {
        server.expect(requestTo(courseUnitRealisationUrl(curId, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                SampleDataFiles.toText("coursecms/" + curId),
                MediaType.APPLICATION_JSON
            ));
    }

    public void expectCourseUnitRealisationRequest(String curId, String responseFile, Locale locale) {
        server.expect(requestTo(courseUnitRealisationUrl(curId, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                SampleDataFiles.toText("coursecms/" + responseFile),
                MediaType.APPLICATION_JSON
            ));
    }

    private String courseUnitRealisationUrl(String curId, Locale locale) {
        String href = String.format("%s/%s/jsonapi/node/course", courseCmsBaseUrl, locale.getLanguage());
        String query =
            URLEncoder.encode("filter[cur-id][condition][path]", StandardCharsets.UTF_8) + "=field_course_unit_realisation_id" +
            "&" + URLEncoder.encode("filter[cur-id][condition][operator]", StandardCharsets.UTF_8) + "=IN" +
            "&" + URLEncoder.encode("filter[cur-id][condition][value][]", StandardCharsets.UTF_8) + "=" + getSisuCourseUnitRealisationId(curId) +
            "&" + URLEncoder.encode("fields[node--course]", StandardCharsets.UTF_8) + "=" + String.join(",", ATTRIBUTES) +
            "&include=field_image" +
            "&jsonapi_include=1";

        return href + "?" + query;
    }
}
