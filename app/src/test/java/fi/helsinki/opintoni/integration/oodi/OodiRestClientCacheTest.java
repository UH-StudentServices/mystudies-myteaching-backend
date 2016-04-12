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

package fi.helsinki.opintoni.integration.oodi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OodiRestClientCacheTest extends SpringTest {

    @Autowired
    private OodiClient oodiESBClient;

    @Test
    public void thatStudentEnrollmentsAreCached() {
        studentRequestChain("123").enrollments();

        List<OodiEnrollment> enrollments = oodiESBClient.getEnrollments("123");
        List<OodiEnrollment> cachedEnrollments = oodiESBClient.getEnrollments("123");

        assertThat(cachedEnrollments).isSameAs(enrollments);
    }

    @Test
    public void thatStudentEventsAreCached() {
        studentRequestChain("123").events();

        List<OodiEvent> events = oodiESBClient.getStudentEvents("123");
        List<OodiEvent> cachedEvents = oodiESBClient.getStudentEvents("123");

        assertThat(cachedEvents).isSameAs(events);
    }

    @Test
    public void thatTeacherCoursesAreCached() {
        teacherRequestChain("123").courses();

        String sinceDateString = DateTimeUtil.getSemesterStartDateString(LocalDate.now());

        List<OodiTeacherCourse> courses = oodiESBClient.getTeacherCourses("123", sinceDateString);
        List<OodiTeacherCourse> cachedCourses = oodiESBClient.getTeacherCourses("123", sinceDateString);

        assertThat(cachedCourses).isSameAs(courses);
    }

    @Test
    public void thatTeacherEventsAreCached() {
        teacherRequestChain("123").events();

        List<OodiEvent> events = oodiESBClient.getTeacherEvents("123");
        List<OodiEvent> cachedEvents = oodiESBClient.getTeacherEvents("123");

        assertThat(cachedEvents).isSameAs(events);
    }
}
