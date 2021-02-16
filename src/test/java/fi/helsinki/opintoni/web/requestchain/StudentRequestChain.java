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
import fi.helsinki.opintoni.server.StudiesServer;
import fi.helsinki.opintoni.web.TestConstants;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_COURSE_REALISATION_ID;

public class StudentRequestChain {
    private final OodiServer oodiServer;
    private final CoursePageServer coursePageServer;
    private final String studentNumber;
    private final StudiesServer studiesServer;

    public StudentRequestChain(String studentNumber, OodiServer oodiServer, CoursePageServer coursePageServer, StudiesServer studiesServer) {
        this.oodiServer = oodiServer;
        this.coursePageServer = coursePageServer;
        this.studentNumber = studentNumber;
        this.studiesServer = studiesServer;
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

    public CourseImplementationRequestChain<StudentRequestChain> courseImplementationWithLocaleRequestChain(
        String coursePageImplementationId, Locale locale, String responseFile) {
        return courseImplementationRequestChain(coursePageImplementationId, locale).expectImplementation(responseFile);
    }

    public CourseImplementationRequestChain<StudentRequestChain> courseImplementation(
        String coursePageImplementationId, String responseFile) {
        return courseImplementationWithLocaleRequestChain(coursePageImplementationId, DEFAULT_USER_LOCALE, responseFile);
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

    public StudentRequestChain attainments() {
        oodiServer.expectStudentStudyAttainmentsRequest(studentNumber);
        return this;
    }

    public StudentRequestChain roles() {
        oodiServer.expectRolesRequest(TestConstants.STUDENT_PERSON_ID);
        return this;
    }

    public StudentRequestChain roles(String personId) {
        oodiServer.expectRolesRequest(personId);
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

    public StudiesRequestChain<StudentRequestChain> defaultCoursePageUrls() throws Exception {
        return defaultCoursePageUrls(new Locale("fi"));
    }

    public StudiesRequestChain<StudentRequestChain> defaultCoursePageUrls(Locale locale) throws Exception {
        return coursePageUrls(Collections.singletonList("hy-CUR-123456789"), locale);
    }

    public StudiesRequestChain<StudentRequestChain> coursePageUrls(List<String> courseIds, Locale locale) throws Exception {
        return studiesRequestChain().expectCoursePageUrls(courseIds, locale);
    }

    public StudiesRequestChain<StudentRequestChain> coursePageUrls(Map<String, String> coursePageUrlsByCourseId, Locale locale) throws Exception {
        return studiesRequestChain().expectCoursePageUrls(coursePageUrlsByCourseId, locale);
    }

    private StudiesRequestChain<StudentRequestChain> studiesRequestChain() {
        return new StudiesRequestChain<>(this, studiesServer);
    }
}
