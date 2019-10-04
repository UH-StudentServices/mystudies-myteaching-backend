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
import fi.helsinki.opintoni.util.DateTimeUtil;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static fi.helsinki.opintoni.web.TestConstants.EMPLOYEE_NUMBER;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_NUMBER;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_PERSON_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class StudyRegistryServiceCacheTest extends SpringTest {
    @Autowired
    private StudyRegistryService studyRegistryService;

    @Test
    public void thatStudentEnrollmentsAreCached() {
        studentRequestChain(STUDENT_NUMBER).enrollments();

        List<Enrollment> enrollments = studyRegistryService.getEnrollments(STUDENT_NUMBER);
        List<Enrollment> cachedEnrollments = studyRegistryService.getEnrollments(STUDENT_NUMBER);

        assertThat(cachedEnrollments).isSameAs(enrollments);
    }

    @Test
    public void thatStudentEventsAreCached() {
        studentRequestChain(STUDENT_NUMBER).events();

        List<Event> events = studyRegistryService.getStudentEvents(STUDENT_NUMBER);
        List<Event> cachedEvents = studyRegistryService.getStudentEvents(STUDENT_NUMBER);

        assertThat(cachedEvents).isSameAs(events);
    }

    @Test
    public void thatTeacherCoursesAreCached() {
        teacherRequestChain(EMPLOYEE_NUMBER).courses();

        String sinceDateString = DateTimeUtil.getSemesterStartDateString(LocalDate.now());

        List<TeacherCourse> courses = studyRegistryService.getTeacherCourses(EMPLOYEE_NUMBER, sinceDateString);
        List<TeacherCourse> cachedCourses = studyRegistryService.getTeacherCourses(EMPLOYEE_NUMBER, sinceDateString);

        assertThat(cachedCourses).isSameAs(courses);
    }

    @Test
    public void thatTeacherEventsAreCached() {
        teacherRequestChain(EMPLOYEE_NUMBER).events();

        List<Event> events = studyRegistryService.getTeacherEvents(EMPLOYEE_NUMBER);
        List<Event> cachedEvents = studyRegistryService.getTeacherEvents(EMPLOYEE_NUMBER);

        assertThat(cachedEvents).isSameAs(events);
    }

    @Test
    public void thatStudyAttainmentsAreCachedByPersonId() {
        studentRequestChain(STUDENT_NUMBER).roles().attainments();

        List<StudyAttainment> attainments =
            studyRegistryService.getStudyAttainments(STUDENT_PERSON_ID);
        List<StudyAttainment> cachedAttainments =
            studyRegistryService.getStudyAttainments(STUDENT_PERSON_ID);

        assertThat(attainments).isSameAs(cachedAttainments);
    }

    @Test
    public void thatStudyAttainmentsAreCachedByPersonIdAndStudentNumber() {
        studentRequestChain(STUDENT_NUMBER).attainments();

        List<StudyAttainment> attainments =
            studyRegistryService.getStudyAttainments(STUDENT_PERSON_ID, STUDENT_NUMBER);
        List<StudyAttainment> cachedAttainments =
            studyRegistryService.getStudyAttainments(STUDENT_PERSON_ID, STUDENT_NUMBER);

        assertThat(attainments).isSameAs(cachedAttainments);
    }

    @Test
    public void thatStudyRightsAreCached() {
        studentRequestChain(STUDENT_NUMBER).studyRights();

        List<StudyRight> studyRights =
            studyRegistryService.getStudentStudyRights(STUDENT_NUMBER);
        List<StudyRight> cachedStudyRights =
            studyRegistryService.getStudentStudyRights(STUDENT_NUMBER);

        assertThat(studyRights).isSameAs(cachedStudyRights);
    }
}
