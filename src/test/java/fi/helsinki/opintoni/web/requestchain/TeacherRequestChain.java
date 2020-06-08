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

import fi.helsinki.opintoni.server.CourseCmsServer;
import fi.helsinki.opintoni.server.CoursePageServer;
import fi.helsinki.opintoni.server.OodiServer;
import fi.helsinki.opintoni.server.SotkaServer;

import java.util.List;
import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static fi.helsinki.opintoni.web.TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID;
import static fi.helsinki.opintoni.web.TestConstants.POSITION_STUDYGROUP_TEACHER_COURSE_REALISATION_ID;
import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;

public class TeacherRequestChain {

    private static final String TEACHER_COURSE_IMPLEMENTATION_FILE = "teacher_course.json";

    private static final List<String> DEFAULT_TEACHER_COURSE_IDS = List.of("99903629", "99903628", "99903630", "1234567");

    private final OodiServer oodiServer;
    private final CoursePageServer coursePageServer;
    private final CourseCmsServer courseCmsServer;
    private final SotkaServer sotkaServer;
    private final String teacherNumber;
    private final String sinceDateString;

    public TeacherRequestChain(String teacherNumber,
                               String sinceDateString,
                               OodiServer oodiServer,
                               CoursePageServer coursePageServer,
                               CourseCmsServer courseCmsServer,
                               SotkaServer sotkaServer) {
        this.oodiServer = oodiServer;
        this.coursePageServer = coursePageServer;
        this.teacherNumber = teacherNumber;
        this.sinceDateString = sinceDateString;
        this.courseCmsServer = courseCmsServer;
        this.sotkaServer = sotkaServer;
    }

    public TeacherRequestChain courses() {
        oodiServer.expectTeacherCoursesRequest(teacherNumber, sinceDateString);
        return this;
    }

    public TeacherRequestChain courses(String responseFile) {
        oodiServer.expectTeacherCoursesRequest(teacherNumber, sinceDateString, responseFile);
        return this;
    }

    public TeacherRequestChain course(String realisationId, String responseFile) {
        oodiServer.expectGdprCourseUnitRealisationRequest(realisationId, responseFile);
        return this;
    }

    public TeacherRequestChain defaultCourses() {
        TeacherRequestChain chain = courses();

        for (String realisationId : DEFAULT_TEACHER_COURSE_IDS) {
            chain.course(realisationId, "normal_courseunitrealisation.json");
        }

        return chain;
    }

    public TeacherRequestChain defaultCoursesWithImplementationsAndRealisations() {
        return defaultCourses()
            .defaultCourseImplementation()
            .and()
            .examCourseImplementation()
            .and()
            .courseImplementationWithRealisationId(POSITION_STUDYGROUP_TEACHER_COURSE_REALISATION_ID)
            .and();
    }

    public TeacherRequestChain openUniversityCourses() {
        return courses("teachercoursesopenuniversity.json").course("105975184", "openuni_courseunitrealisation.json");
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
        return courseImplementation(courseImplementationId, responseFile, DEFAULT_USER_LOCALE);
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

    public CourseCmsRequestChain<TeacherRequestChain> courseCmsCourseUnitRealisation(Locale locale) {
        return courseCmsCourseUnitRealisation(TEACHER_COURSE_REALISATION_ID, locale);
    }

    public CourseCmsRequestChain<TeacherRequestChain> courseCmsCourseUnitRealisation(
        String courseUnitRealisationId, Locale locale) {
        return courseCmsCourseUnitRealisation(courseUnitRealisationId, "course.json", locale);
    }

    public CourseCmsRequestChain<TeacherRequestChain> courseCmsCourseUnitRealisation(
        String courseUnitRealisationId, String responseFile, Locale locale) {
        CourseCmsRequestChain<TeacherRequestChain> builder = new CourseCmsRequestChain<>(
            this,
            courseCmsServer,
            courseUnitRealisationId,
            locale
        );
        return builder.expectCourseUnitRealisation(responseFile);
    }

    public SotkaRequestChain<TeacherRequestChain> oodiHierarchy() {
        return oodiHierarchy(TEACHER_COURSE_REALISATION_ID);
    }

    public SotkaRequestChain<TeacherRequestChain> oodiHierarchy(String oodiRealisationId) {
        return oodiHierarchy(oodiRealisationId, "oodi_hierarchy.json");
    }

    public SotkaRequestChain<TeacherRequestChain> oodiHierarchy(String oodiRealisationId, String responseFile) {
        SotkaRequestChain<TeacherRequestChain> builder = new SotkaRequestChain<>(
            sotkaServer,
            this,
            oodiRealisationId
        );
        return builder.expectOodiHieracry(responseFile);
    }
}
