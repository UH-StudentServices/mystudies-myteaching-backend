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

package fi.helsinki.opintoni.integration.studyregistry;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyRegistryServiceCacheTest extends SpringTest {
    @Autowired
    private StudyRegistryService studyRegistryService;

    @Test
    public void thatStudentEnrollmentsAreCached() {
        studentRequestChain("123").enrollments();

        List<Enrollment> enrollments = studyRegistryService.getEnrollments("123");
        List<Enrollment> cachedEnrollments = studyRegistryService.getEnrollments("123");

        assertThat(cachedEnrollments).isSameAs(enrollments);
    }

    @Test
    public void thatStudentEventsAreCached() {
        studentRequestChain("123").events();

        List<Event> events = studyRegistryService.getStudentEvents("123");
        List<Event> cachedEvents = studyRegistryService.getStudentEvents("123");

        assertThat(cachedEvents).isSameAs(events);
    }

    @Test
    public void thatTeacherCoursesAreCached() {
        teacherRequestChain("123").courses();

        String sinceDateString = DateTimeUtil.getSemesterStartDateString(LocalDate.now());

        List<TeacherCourse> courses = studyRegistryService.getTeacherCourses("123", sinceDateString);
        List<TeacherCourse> cachedCourses = studyRegistryService.getTeacherCourses("123", sinceDateString);

        assertThat(cachedCourses).isSameAs(courses);
    }

    @Test
    public void thatTeacherEventsAreCached() {
        teacherRequestChain("123").events();

        List<Event> events = studyRegistryService.getTeacherEvents("123");
        List<Event> cachedEvents = studyRegistryService.getTeacherEvents("123");

        assertThat(cachedEvents).isSameAs(events);
    }
}
