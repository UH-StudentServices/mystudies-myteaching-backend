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

public class FlammaServer {

    private final MockRestServiceServer server;

    private final String studentNewsPathFi;
    private static final String STUDENT_NEWS_FILE_FI = "newsfeeds/flamma/studentnews.xml";

    private final String studentNewsPathEn;
    private static final String STUDENT_NEWS_FILE_EN = "newsfeeds/flamma/englishstudentnews.xml";

    private final String teacherNewsPathFi;
    private static final String TEACHER_NEWS_FILE_FI = "newsfeeds/flamma/teachernews.xml";

    private final String teacherNewsPathEn;
    private static final String TEACHER_NEWS_FILE_EN = "newsfeeds/flamma/englishteachernews.xml";

    public FlammaServer(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.studentNewsPathFi = appConfiguration.get("newsfeeds.studentFeedsByLocale.fi");
        this.studentNewsPathEn = appConfiguration.get("newsfeeds.studentFeedsByLocale.en");
        this.teacherNewsPathFi = appConfiguration.get("newsfeeds.teacherFeedsByLocale.fi");
        this.teacherNewsPathEn = appConfiguration.get("newsfeeds.teacherFeedsByLocale.en");
    }

    public void expectStudentNews() {
        server.expect(requestTo(studentNewsPathFi))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(STUDENT_NEWS_FILE_FI), MediaType.TEXT_XML));
    }

    public void expectEnglishStudentNews() {
        server.expect(requestTo(studentNewsPathEn))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(STUDENT_NEWS_FILE_EN), MediaType.TEXT_XML));
    }

    public void expectTeacherNews() {
        server.expect(requestTo(teacherNewsPathFi))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(TEACHER_NEWS_FILE_FI), MediaType.TEXT_XML));
    }

    public void expectEnglishTeacherNews() {
        server.expect(requestTo(teacherNewsPathEn))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(TEACHER_NEWS_FILE_EN), MediaType.TEXT_XML));
    }

}
