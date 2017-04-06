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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class OodiServer {

    private final MockRestServiceServer server;
    private final String oodiBaseUrl;

    public OodiServer(AppConfiguration appConfiguration,
                      RestTemplate oodiRestTemplate) {
        this.server = MockRestServiceServer.createServer(oodiRestTemplate);
        this.oodiBaseUrl = appConfiguration.get("oodi.base.url");
    }

    public void expectStudentEnrollmentsRequest(String studentNumber) {
        server.expect(requestTo(enrollmentsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/enrollments.json"), MediaType.APPLICATION_JSON));
    }

    public void expectStudentEnrollmentsRequest(String studentNumber, String responseFile) {
        server.expect(requestTo(enrollmentsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/" + responseFile), MediaType.APPLICATION_JSON));
    }

    public void expectStudentEventsRequest(String studentNumber) {
        server.expect(requestTo(eventsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/studentevents.json"), MediaType.APPLICATION_JSON));
    }

    public void expectTeacherEventsRequest(String teacherNumber) {
        server.expect(requestTo(teacherEventsUrl(teacherNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/teacherevents.json"), MediaType.APPLICATION_JSON));
    }

    public void expectTeacherCoursesRequest(String teacherNumber, String sinceDateString) {
        server.expect(requestTo(teachingUrl(teacherNumber, sinceDateString)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/teachercourses.json"), MediaType.APPLICATION_JSON));
    }

    public void expectTeacherCoursesRequest(String teacherNumber, String sinceDateString, String responseFile) {
        server.expect(requestTo(teachingUrl(teacherNumber, sinceDateString)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(SampleDataFiles.toText("oodi/" + responseFile), MediaType.APPLICATION_JSON));
    }

    public void expectStudentStudyAttainmentsRequest(String studentNumber) {
        server.expect(requestTo(studyAttainmentsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/studyattainments.json"),
                    MediaType.APPLICATION_JSON)
            );
    //TODO: DELETE SECOND CALL WHEN OODI RETURNS LOCALIZED GRADES
        server.expect(requestTo(studyAttainmentsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/studyattainments.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    //TODO: DELETE METHOD AND studyattainments_old.json WHEN OODI RETURNS LOCALIZED GRADES
    public void expectOldStudentStudyAttainmentsRequest(String studentNumber) {
        server.expect(requestTo(studyAttainmentsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/studyattainments_old.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectStudentStudyRightsRequest(String studentNumber) {
        server.expect(requestTo(studyRightsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/studentstudyrights.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectStudentStudyRightsRequest(String studentNumber, String responseFile) {
        server.expect(requestTo(studyRightsUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "oodi/courseunitrealisation.json");
    }

    public void expectCancelledCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "oodi/cancelledcourseunitrealisation.json");
    }

    public void expectPositionStudygroupsetCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "oodi/courseunitrealisation_position_studygroupset.json");
    }

    public void expectPositionStudygroupCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "oodi/courseunitrealisation_position_studygroup.json");
    }

    private void courseUnitRealisationRequest(String realisationId, String fileName) {
        server.expect(requestTo(courseUnitRealisationUrl(realisationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText(fileName),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectCourseUnitRealisationRequest(String realisationId, String responseFile) {
        server.expect(requestTo(courseUnitRealisationUrl(realisationId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectCourseNameRequest(String learningOpportunityId, String responseFile) {
        server.expect(requestTo(learningOpportunityUrl(learningOpportunityId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                SampleDataFiles.toText("oodi/" + responseFile),
                MediaType.APPLICATION_JSON)
            );
    }

    public void expectStudentInfo(String studentNumber) {
        server.expect(requestTo(studentInfoUrl(studentNumber)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/studentinfo.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectRolesRequest(String oodiPersonId) {
        server.expect(requestTo(rolesUrl(oodiPersonId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/roles.json"),
                    MediaType.APPLICATION_JSON)
            );
    }

    public void expectRolesRequest(String oodiPersonId, String responseFile) {
        server.expect(requestTo(rolesUrl(oodiPersonId)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                    SampleDataFiles.toText("oodi/" + responseFile),
                    MediaType.APPLICATION_JSON)
            );
    }

    private String buildingsUrl() {
        return oodiBaseUrl + "/codes/buildings";
    }

    private String studyRightsUrl(String studentNumber) {
        return oodiBaseUrl + "/students/" + studentNumber + "/studyrights";
    }

    private String studyAttainmentsUrl(String studentNumber) {
        return oodiBaseUrl + "/students/" + studentNumber + "/studyattainments";
    }

    private String teachingUrl(String teacherNumber, String sinceDateString) {
        return oodiBaseUrl + "/teachers/" + teacherNumber + "/teaching/all?since_date=" + sinceDateString;
    }

    private String enrollmentsUrl(String studentNumber) {
        return oodiBaseUrl + "/students/" + studentNumber + "/enrollments";
    }

    private String eventsUrl(String studentNumber) {
        return oodiBaseUrl + "/students/" + studentNumber + "/events";
    }

    private String studentInfoUrl(String studentNumber) {
        return oodiBaseUrl + "/students/" + studentNumber + "/info";
    }

    private String teacherEventsUrl(String teacherNumber) {
        return oodiBaseUrl + "/teachers/" + teacherNumber + "/events";
    }

    private String courseUnitRealisationUrl(String realisationId) {
        return oodiBaseUrl + "/courseunitrealisations/" + realisationId;
    }

    private String learningOpportunityUrl(String learningOpportunityId) {
        return oodiBaseUrl + "/learningopportunities/" + learningOpportunityId;
    }

    private String rolesUrl(String oodiPersonId) {
        return oodiBaseUrl + "/persons/" + oodiPersonId + "/roles";
    }
}
