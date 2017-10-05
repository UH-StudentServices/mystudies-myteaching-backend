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
    private final String flammaBaseUrl;

    private static final String STUDENT_NEWS_PATH_FI = "/infotaulu/atom-news.xml";
    private static final String STUDENT_NEWS_FILE_FI = "flamma/studentnews.xml";

    private static final String STUDENT_NEWS_PATH_EN = "/infotaulu/atom-news-en.xml";
    private static final String STUDENT_NEWS_FILE_EN = "flamma/englishstudentnews.xml";

    private static final String TEACHER_NEWS_PATH_FI = "/infotaulu/atom-tiedotteet-opetusasiat.xml";
    private static final String TEACHER_NEWS_FILE_FI = "flamma/teachernews.xml";

    private static final String TEACHER_NEWS_PATH_EN = "/infotaulu/atom-tiedotteet-opetusasiat-en.xml";
    private static final String TEACHER_NEWS_FILE_EN = "flamma/englishteachernews.xml";

    public FlammaServer(AppConfiguration appConfiguration, RestTemplate restTemplate) {
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.flammaBaseUrl = appConfiguration.get("flamma.base.url");
    }

    public void expectStudentNews() {
        server.expect(requestTo(flammaBaseUrl + STUDENT_NEWS_PATH_FI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(STUDENT_NEWS_FILE_FI), MediaType.TEXT_XML));
    }

    public void expectEnglishStudentNews() {
        server.expect(requestTo(flammaBaseUrl + STUDENT_NEWS_PATH_EN))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(STUDENT_NEWS_FILE_EN), MediaType.TEXT_XML));
    }

    public void expectTeacherNews() {
        server.expect(requestTo(flammaBaseUrl + TEACHER_NEWS_PATH_FI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(TEACHER_NEWS_FILE_FI), MediaType.TEXT_XML));
    }

    public void expectEnglishTeacherNews() {
        server.expect(requestTo(flammaBaseUrl + TEACHER_NEWS_PATH_EN))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(toText(TEACHER_NEWS_FILE_EN), MediaType.TEXT_XML));
    }

}
