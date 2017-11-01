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

import java.util.Arrays;
import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.*;

public class TeacherRequestChain {

    private static final String TEACHER_COURSE_IMPLEMENTATION_FILE = "teacher_course.json";

    private final OodiServer oodiServer;
    private final CoursePageServer coursePageServer;
    private final String teacherNumber;
    private final String sinceDateString;

    public TeacherRequestChain(String teacherNumber, String sinceDateString, OodiServer oodiServer, CoursePageServer coursePageServer) {
        this.oodiServer = oodiServer;
        this.coursePageServer = coursePageServer;
        this.teacherNumber = teacherNumber;
        this.sinceDateString = sinceDateString;
    }

    public TeacherRequestChain courses() {
        oodiServer.expectTeacherCoursesRequest(teacherNumber, sinceDateString);
        return this;
    }

    public TeacherRequestChain defaultCoursesWithImplementationsAndRealisations() {
        return courses()
            .defaultCourseImplementation()
            .and()
            .examCourseImplementation()
            .and()
            .courseImplementationWithRealisationId(POSITION_STUDYGROUP_TEACHER_COURSE_REALISATION_ID)
            .and();
    }

    public TeacherRequestChain courses(String responseFile) {
        oodiServer.expectTeacherCoursesRequest(teacherNumber, sinceDateString, responseFile);
        return this;
    }

    public TeacherRequestChain events() {
        oodiServer.expectTeacherEventsRequest(teacherNumber);
        return this;
    }

    public CourseImplementationRequestChain<TeacherRequestChain> defaultCourseImplementation() {
        return courseImplementationWithRealisationId(TEACHER_COURSE_REALISATION_ID);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> courseImplementationWithLocale(Locale locale) {
        return courseImplementation(TEACHER_COURSE_REALISATION_ID, TEACHER_COURSE_IMPLEMENTATION_FILE, locale);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> examCourseImplementation() {
        return courseImplementationWithRealisationId(EXAM_TEACHER_COURSE_REALISATION_ID);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> courseImplementationWithRealisationId(String realisationId) {
        return courseImplementation(realisationId, TEACHER_COURSE_IMPLEMENTATION_FILE);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> courseImplementation(
        String courseImplementationId, String responseFile) {
        return courseImplementation(courseImplementationId, responseFile, DEFAULT_COURSE_IMPLEMENTATION_LOCALE);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> courseImplementation(
        String courseImplementationId, String responseFile, Locale locale) {
        CourseImplementationRequestChain<TeacherRequestChain> builder =
            new CourseImplementationRequestChain<>(
                this,
                coursePageServer,
                courseImplementationId,
                locale);
        return builder.expectImplementation(responseFile);
    }
}
