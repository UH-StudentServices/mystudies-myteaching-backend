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
import java.util.Locale;

import static org.junit.Assert.assertSame;

public class OodiRestClientCacheTest extends SpringTest {

    @Autowired
    private OodiClient oodiRestClient;

    @Test
    public void thatStudentEnrollmentsAreCached() {
        studentRequestChain("123").enrollments();

        List<OodiEnrollment> enrollments = oodiRestClient.getEnrollments("123", Locale.ENGLISH);
        List<OodiEnrollment> cachedEnrollments = oodiRestClient.getEnrollments("123", Locale.ENGLISH);

        assertSame(enrollments, cachedEnrollments);
    }

    @Test
    public void thatStudentEventsAreCached() {
        studentRequestChain("123").events();

        List<OodiEvent> events = oodiRestClient.getStudentEvents("123", Locale.ENGLISH);
        List<OodiEvent> cachedEvents = oodiRestClient.getStudentEvents("123", Locale.ENGLISH);

        assertSame(events, cachedEvents);
    }

    @Test
    public void thatTeacherCoursesAreCached() {
        teacherRequestChain("123").courses();

        String sinceDateString = DateTimeUtil.getLastSemesterStartDateString(LocalDate.now());

        List<OodiTeacherCourse> courses = oodiRestClient.getTeacherCourses("123", Locale.ENGLISH, sinceDateString);
        List<OodiTeacherCourse> cachedCourses = oodiRestClient.getTeacherCourses("123", Locale.ENGLISH, sinceDateString);

        assertSame(courses, cachedCourses);
    }

    @Test
    public void thatTeacherEventsAreCached() {
        teacherRequestChain("123").events();

        List<OodiEvent> events = oodiRestClient.getTeacherEvents("123", Locale.ENGLISH);
        List<OodiEvent> cachedEvents = oodiRestClient.getTeacherEvents("123", Locale.ENGLISH);

        assertSame(events, cachedEvents);
    }
}
