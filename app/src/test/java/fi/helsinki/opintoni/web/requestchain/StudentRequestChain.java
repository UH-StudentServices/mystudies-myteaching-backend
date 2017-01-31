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

package fi.helsinki.opintoni.web.requestchain;

import fi.helsinki.opintoni.server.CoursePageServer;
import fi.helsinki.opintoni.server.OodiServer;
import fi.helsinki.opintoni.web.TestConstants;

import static java.util.Collections.singletonList;

public class StudentRequestChain {

    private final OodiServer oodiServer;
    private final CoursePageServer coursePageServer;
    private final String studentNumber;

    public StudentRequestChain(String studentNumber, OodiServer oodiServer, CoursePageServer coursePageServer) {
        this.oodiServer = oodiServer;
        this.coursePageServer = coursePageServer;
        this.studentNumber = studentNumber;
    }

    public StudentRequestChain events() {
        oodiServer.expectStudentEventsRequest(studentNumber);
        return this;
    }

    public CourseImplementationRequestChain<StudentRequestChain> defaultImplementation() {
        return courseImplementation(TestConstants.STUDENT_COURSE_REALISATION_ID);
    }

    public CourseImplementationRequestChain<StudentRequestChain> courseImplementation(
        String coursePageImplementationId, String responseFile) {
        return courseImplementationRequestChain(coursePageImplementationId).expectImplementation(responseFile);
    }

    public CourseImplementationRequestChain<StudentRequestChain> courseImplementation(
        String coursePageImplementationId) {
        return courseImplementationRequestChain(coursePageImplementationId).expectImplementation();
    }

    private CourseImplementationRequestChain<StudentRequestChain> courseImplementationRequestChain(String coursePageImplementationId) {
        return new CourseImplementationRequestChain<>(
            this,
            coursePageServer,
            coursePageImplementationId);
    }

    public CourseImplementationActivityRequestChain<StudentRequestChain> activity(String responseFile) {
        CourseImplementationActivityRequestChain<StudentRequestChain> chain =
            new CourseImplementationActivityRequestChain<>(
                this,
                coursePageServer,
                singletonList(TestConstants.STUDENT_COURSE_REALISATION_ID)
            );
        return chain.activity(responseFile);
    }

    public StudentRequestChain enrollments() {
        oodiServer.expectStudentEnrollmentsRequest(studentNumber);
        return this;
    }

    public StudentRequestChain enrollments(String responseFile) {
        oodiServer.expectStudentEnrollmentsRequest(studentNumber, responseFile);
        return this;
    }

    public StudentRequestChain defaultCourseUnitRealisation() {
        oodiServer.expectCourseUnitRealisationRequest(TestConstants.STUDENT_COURSE_REALISATION_ID);
        return this;
    }

    public StudentRequestChain cancelledCourseUnitRealisation() {
        oodiServer.expectCancelledCourseUnitRealisationRequest(TestConstants.STUDENT_COURSE_REALISATION_ID);
        return this;
    }

    public StudentRequestChain positionStudygroupsetCourseUnitRealisation() {
        oodiServer.expectPositionStudygroupsetCourseUnitRealisationRequest(TestConstants.STUDENT_COURSE_REALISATION_ID);
        return this;
    }

    public StudentRequestChain defaultOneOffEvents() {
        coursePageServer.expectStudentCourseImplementationEventsRequest(TestConstants.STUDENT_COURSE_REALISATION_ID);
        return this;
    }

    public StudentRequestChain attainments() {
        oodiServer.expectStudentStudyAttainmentsRequest(studentNumber);
        return this;
    }

    // DELETE METHOD WHEN OODI RETURNS LOCALIZED GRADES
    public StudentRequestChain oldAttainments() {
        oodiServer.expectOldStudentStudyAttainmentsRequest(studentNumber);
        return this;
    }

    public StudentRequestChain roles() {
        oodiServer.expectRolesRequest(TestConstants.STUDENT_OODI_PERSON_ID);
        return this;
    }

    public StudentRequestChain roles(String responseFile) {
        oodiServer.expectRolesRequest(TestConstants.STUDENT_OODI_PERSON_ID, responseFile);
        return this;
    }

    public StudentRequestChain info() {
        oodiServer.expectStudentInfo(studentNumber);
        return this;
    }

    public StudentRequestChain studyRights() {
        oodiServer.expectStudentStudyRightsRequest(studentNumber);
        return this;
    }

    public StudentRequestChain studyRights(String responseFile) {
        oodiServer.expectStudentStudyRightsRequest(studentNumber, responseFile);
        return this;
    }
}
