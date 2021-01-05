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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.config.AppConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class StudiesServer extends AbstractRestServiceServer {
    private final String studiesApiBseUrl;
    private final ObjectMapper objectMapper;

    public StudiesServer(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        super(MockRestServiceServer.createServer(restTemplate));
        this.studiesApiBseUrl = appConfiguration.get("studies.client.apiUrl");
        this.objectMapper = new ObjectMapper();
    }

    public void expectCoursePageUrlsRequest(List<String> courseIds, Locale locale) throws Exception {
        Map<String, String> response = courseIds.stream().collect(toMap(Function.identity(), this::courseIdToCoursePageUrl));
        server.expect(requestTo(coursePageUrlsUrl(courseIds, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
    }

    public void expectCoursePageUrlsRequest(Map<String, String> coursePageUrlsByCourseId, Locale locale) throws JsonProcessingException {
        List<String> courseIds = new ArrayList<>(coursePageUrlsByCourseId.keySet());
        server.expect(requestTo(coursePageUrlsUrl(courseIds, locale)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(objectMapper.writeValueAsString(coursePageUrlsByCourseId), MediaType.APPLICATION_JSON));
    }

    private String coursePageUrlsUrl(List<String> courseIds, Locale locale) {
        return UriComponentsBuilder.fromHttpUrl(studiesApiBseUrl).path("/courses/url")
            .queryParam("courseId", courseIds.toArray())
            .queryParam("languageCode", locale.getLanguage())
            .build().toUriString();
    }

    private String courseIdToCoursePageUrl(String courseId) {
        return "http://local.studies.helsinki.fi/opintotarjonta/cur/" + courseId;
    }
}
