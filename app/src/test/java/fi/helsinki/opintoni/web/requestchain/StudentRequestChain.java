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

import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_COURSE_REALISATION_ID;

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
        return courseImplementationRequestChain(STUDENT_COURSE_REALISATION_ID, DEFAULT_USER_LOCALE)
            .expectImplementation();
    }

    public CourseImplementationRequestChain<StudentRequestChain> defaultImplementationWithLocale(Locale locale) {
        return courseImplementationRequestChain(STUDENT_COURSE_REALISATION_ID, locale)
            .expectImplementation();
    }

    public CourseImplementationRequestChain<StudentRequestChain> courseImplementation(
        String coursePageImplementationId, String responseFile) {
        return courseImplementationRequestChain(coursePageImplementationId, DEFAULT_USER_LOCALE).expectImplementation(responseFile);
    }

    private CourseImplementationRequestChain<StudentRequestChain> courseImplementationRequestChain(String coursePageImplementationId, Locale locale) {
        return new CourseImplementationRequestChain<>(
            this,
            coursePageServer,
            coursePageImplementationId,
            locale);
    }

    public StudentRequestChain enrollments() {
        oodiServer.expectStudentEnrollmentsRequest(studentNumber);
        return this;
    }

    public StudentRequestChain enrollments(String responseFile) {
        oodiServer.expectStudentEnrollmentsRequest(studentNumber, responseFile);
        return this;
    }

    public StudentRequestChain defaultCourseUnitRealisationTeachers() {
        oodiServer.expectCourseUnitRealisationTeachersRequest(STUDENT_COURSE_REALISATION_ID);
        return this;
    }

    public StudentRequestChain attainments() {
        oodiServer.expectStudentStudyAttainmentsRequest(studentNumber);
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
