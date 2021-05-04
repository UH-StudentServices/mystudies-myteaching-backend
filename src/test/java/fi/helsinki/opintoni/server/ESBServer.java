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

import static fi.helsinki.opintoni.service.profile.EmployeeContactInformationService.EMPLOYEE_NUMBER_PREFIX;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ESBServer extends AbstractRestServiceServer {

    private final String baseUrl;

    public ESBServer(AppConfiguration appConfiguration,
                     RestTemplate esbRestTemplate) {
        super(MockRestServiceServer.createServer(esbRestTemplate));

        this.baseUrl = appConfiguration.get("esb.base.url");
    }

    public void expectEmployeeContactInformationRequest(String employeeNumber) {
        server.expect(requestTo(String.format("%s/person/opetukseni/employee/%s%s",
            baseUrl,
            EMPLOYEE_NUMBER_PREFIX,
            employeeNumber)))
            .andExpect(header("Apikey", "abloy"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("esb/employeeinfo.json"), MediaType.APPLICATION_JSON));
    }

    public void expectTeacherCalendarRequest(String teacherNumber) {
        server.expect(requestTo(optimeTeacherRequestUrl(teacherNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("esb/calendar.json"), MediaType.APPLICATION_JSON));
    }

    public void expectFailedTeacherCalendarRequest(String teacherNumber) {
        server.expect(requestTo(optimeTeacherRequestUrl(teacherNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("esb/calendar-empty.json"), MediaType.APPLICATION_JSON));
    }

    private String optimeTeacherRequestUrl(String teacherNumber) {
        return String.format("%s/optime/staff/%s", baseUrl, teacherNumber);
    }

}
