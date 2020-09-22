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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import fi.helsinki.opintoni.web.TestConstants;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CourseServiceTest extends SpringTest {

    @Autowired
    private CourseService courseService;

    @MockBean
    SisuStudyRegistry mockSisyStudyRegistry;

    @MockBean
    CourseCmsClient mockCourseCmsClient;

    @Test
    public void thatStudentCourseDtosAreFetched() {
        expectStudentCourses();

        Set<CourseDto> courseDtos = courseService
            .getCourses(Optional.of(TestConstants.STUDENT_NUMBER), Optional.empty(), DEFAULT_USER_LOCALE);

        assertThat(courseDtos).hasSize(1);
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.STUDENT_COURSE_REALISATION_ID));
        assertThat(courseDtos.iterator().next().teachers.get(0)).isEqualTo("Rantala Kari A");
    }

    @Test
    public void thatTeacherCourseDtosAreFetched() {
        when(mockSisyStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class))).thenReturn(
            List.of(course(TestConstants.TEACHER_COURSE_REALISATION_ID),
                course(TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID)));

        Set<CourseDto> courseDtos = courseService
            .getCourses(Optional.empty(), Optional.of(TestConstants.EMPLOYEE_NUMBER), DEFAULT_USER_LOCALE);

        assertThat(courseDtos).hasSize(2);
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.TEACHER_COURSE_REALISATION_ID));
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID));
    }

    private TeacherCourse course(String teacherCourseRealisationId) {
        TeacherCourse course = new TeacherCourse();
        course.realisationId = teacherCourseRealisationId;
        course.startDate = LocalDate.of(2020, 8, 1).atStartOfDay();
        course.startDate = LocalDate.of(2020, 9, 1).atStartOfDay();
        return course;
    }

    @Test
    public void thatStudentAndTeacherCourseDtosAreFetched() {
        expectStudentCourses();
        when(mockSisyStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class))).thenReturn(
            List.of(course(TestConstants.TEACHER_COURSE_REALISATION_ID),
                course(TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID)));

        Set<CourseDto> courseDtos = courseService
            .getCourses(
                Optional.of(TestConstants.STUDENT_NUMBER),
                Optional.of(TestConstants.EMPLOYEE_NUMBER),
                DEFAULT_USER_LOCALE);

        assertThat(courseDtos).hasSize(3);
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.STUDENT_COURSE_REALISATION_ID));
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.TEACHER_COURSE_REALISATION_ID));
        assertThat(courseDtos, hasCourseWithRealisationId(TestConstants.EXAM_TEACHER_COURSE_REALISATION_ID));
    }

    private Matcher<Set<CourseDto>> hasCourseWithRealisationId(String realisationId) {
        return new CourseDtoRealisationIdMatcher(realisationId);
    }

    static class CourseDtoRealisationIdMatcher extends TypeSafeMatcher<Set<CourseDto>> {

        private final String realisationId;

        CourseDtoRealisationIdMatcher(String realisationId) {
            this.realisationId = realisationId;
        }

        @Override
        protected boolean matchesSafely(Set<CourseDto> courseDtos) {
            return courseDtos.stream().filter(courseDto -> courseDto.realisationId.equals(realisationId)).count() > 0;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("Set did not contain course with realisationId %s", realisationId));
        }
    }

    private void expectStudentCourses() {
        defaultStudentRequestChain()
            .enrollments()
            .defaultCourseUnitRealisationTeachers()
            .defaultImplementation();
    }

}
