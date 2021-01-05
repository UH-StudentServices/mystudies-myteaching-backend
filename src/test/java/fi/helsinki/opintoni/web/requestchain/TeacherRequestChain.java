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

import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.server.CourseCmsServer;
import fi.helsinki.opintoni.server.CoursePageServer;
import fi.helsinki.opintoni.server.StudiesServer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static fi.helsinki.opintoni.web.TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID;
import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;

public class TeacherRequestChain {

    private static final String TEACHER_COURSE_IMPLEMENTATION_FILE = "teacher_course.json";
    private static final String STRIPPED_CUR_ID = IntegrationUtil.stripPossibleSisuOodiCurPrefix(TEACHER_COURSE_REALISATION_ID);

    private final CoursePageServer coursePageServer;
    private final CourseCmsServer courseCmsServer;
    private final StudiesServer studiesServer;

    public TeacherRequestChain(CoursePageServer coursePageServer, CourseCmsServer courseCmsServer, StudiesServer studiesServer) {
        this.coursePageServer = coursePageServer;
        this.courseCmsServer = courseCmsServer;
        this.studiesServer = studiesServer;
    }

    public CourseImplementationRequestChain<TeacherRequestChain> defaultCourseImplementation() {
        return courseImplementationWithRealisationId(STRIPPED_CUR_ID);
    }

    public CourseImplementationRequestChain<TeacherRequestChain> courseImplementationWithLocale(Locale locale) {
        return courseImplementation(STRIPPED_CUR_ID, TEACHER_COURSE_IMPLEMENTATION_FILE, locale);
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

    public StudiesRequestChain<TeacherRequestChain> defaultCoursePageUrls() throws Exception {
        return defaultCoursePageUrls(new Locale("fi"));
    }

    public StudiesRequestChain<TeacherRequestChain> defaultCoursePageUrls(Locale locale) throws Exception {
        return coursePageUrls(Collections.singletonList("hy-CUR-123456789"), locale);
    }

    public StudiesRequestChain<TeacherRequestChain> coursePageUrls(List<String> courseIds, Locale locale) throws Exception {
        return studiesRequestChain().expectCoursePageUrls(courseIds, locale);
    }

    public StudiesRequestChain<TeacherRequestChain> coursePageUrls(Map<String, String> coursePageUrlsByCourseId, Locale locale) throws Exception {
        return studiesRequestChain().expectCoursePageUrls(coursePageUrlsByCourseId, locale);
    }

    private StudiesRequestChain<TeacherRequestChain> studiesRequestChain() {
        return new StudiesRequestChain<>(this, studiesServer);
    }
}
