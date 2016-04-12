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

import fi.helsinki.opintoni.integration.oodi.mock.OodiMockServer;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;

import static org.mockito.Mockito.*;

public class OodiServer {

    private final OodiMockServer oodiMockServer;

    public OodiServer(OodiMockServer oodiMockServer) {
        this.oodiMockServer = oodiMockServer;
    }

    public void expectStudentEnrollmentsRequest(String studentNumber) {
        when(oodiMockServer.getStudentCourses(studentNumber)).thenReturn(jsonStringResponse("enrollments.json"));
    }

    public void expectStudentEnrollmentsRequest(String studentNumber, String responseFile) {
        when(oodiMockServer.getStudentCourses(studentNumber)).thenReturn(jsonStringResponse(responseFile));
    }

    public void expectStudentEventsRequest(String studentNumber) {
        when(oodiMockServer.getStudentEvents(studentNumber)).thenReturn(jsonStringResponse("studentevents.json"));
    }

    public void expectTeacherEventsRequest(String teacherNumber) {
        when(oodiMockServer.getTeacherEvents(teacherNumber)).thenReturn(jsonStringResponse("teacherevents.json"));
    }

    public void expectTeacherCoursesRequest(String teacherNumber, String sinceDateString) {
        when(oodiMockServer.getTeacherTeaching(teacherNumber, sinceDateString)).thenReturn(jsonStringResponse("teachercourses.json"));
    }

    public void expectTeacherCoursesRequest(String teacherNumber, String sinceDateString, String responseFile) {
        when(oodiMockServer.getTeacherTeaching(teacherNumber, sinceDateString)).thenReturn(jsonStringResponse(responseFile));
    }

    public void expectStudentStudyAttainmentsRequest(String studentNumber) {
        when(oodiMockServer.getStudentStudyAttainments(studentNumber)).thenReturn(jsonStringResponse("studyattainments.json"));
    }

    public void expectStudentStudyRightsRequest(String studentNumber) {
        when(oodiMockServer.getStudentStudyRights(studentNumber)).thenReturn(jsonStringResponse("studentstudyrights.json"));
    }

    public void expectStudentStudyRightsRequest(String studentNumber, String responseFile) {
        when(oodiMockServer.getStudentStudyRights(studentNumber)).thenReturn(jsonStringResponse(responseFile));
    }

    public void expectCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "courseunitrealisation.json");
    }

    public void expectCancelledCourseUnitRealisationRequest(String realisationId) {
        courseUnitRealisationRequest(realisationId, "cancelledcourseunitrealisation.json");
    }

    private void courseUnitRealisationRequest(String realisationId, String responseFile) {
        when(oodiMockServer.getCourseUnitRealisation(realisationId)).thenReturn(jsonStringResponse(responseFile));
    }

    public void expectStudentInfo(String studentNumber) {
        when(oodiMockServer.getStudentInfo(studentNumber)).thenReturn(jsonStringResponse("studentinfo.json"));
    }

    public void expectRolesRequest(String oodiPersonId) {
        when(oodiMockServer.getRoles(oodiPersonId)).thenReturn(jsonStringResponse("roles.json"));
    }

    public void expectRolesRequest(String oodiPersonId, String responseFile) {
        when(oodiMockServer.getRoles(oodiPersonId)).thenReturn(jsonStringResponse(responseFile));
    }

    private String jsonStringResponse(String fileName) {
        return SampleDataFiles.toText("oodi/" + fileName);
    }
}
