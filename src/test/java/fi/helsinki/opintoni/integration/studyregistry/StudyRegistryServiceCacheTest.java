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

import static fi.helsinki.opintoni.web.TestConstants.EMPLOYEE_NUMBER;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_NUMBER;
import static fi.helsinki.opintoni.web.TestConstants.STUDENT_PERSON_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import fi.helsinki.opintoni.util.DateTimeUtil;

public class StudyRegistryServiceCacheTest extends SpringTest {
    @Autowired
    private StudyRegistryService studyRegistryService;

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

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
        LocalDate sinceDate = DateTimeUtil.getSemesterStartDate(LocalDate.now());
        when(mockSisuStudyRegistry.getTeacherCourses(EMPLOYEE_NUMBER, sinceDate)).thenReturn(List.of());
        studyRegistryService.getTeacherCourses(EMPLOYEE_NUMBER, sinceDate);
        studyRegistryService.getTeacherCourses(EMPLOYEE_NUMBER, sinceDate);
        verify(mockSisuStudyRegistry, times(1)).getTeacherCourses(EMPLOYEE_NUMBER, sinceDate);
    }

    @Test
    public void thatTeacherEventsAreCached() {
        when(mockSisuStudyRegistry.getTeacherEvents(EMPLOYEE_NUMBER)).thenReturn(List.of());
        studyRegistryService.getTeacherEvents(EMPLOYEE_NUMBER);
        studyRegistryService.getTeacherEvents(EMPLOYEE_NUMBER);
        verify(mockSisuStudyRegistry, times(1)).getTeacherEvents(EMPLOYEE_NUMBER);
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

    @Test
    public void thatPersonIdIsTransformedToOodiFormatWhenFetchingStudyAttainments() {
        studentRequestChain(STUDENT_NUMBER).roles("1234").attainments();

        studyRegistryService.getStudyAttainments("hy-hlo-1234");
    }

    @Test
    public void thatOodiPersonIdStillWorksForFetchingStudyAttainments() {
        studentRequestChain(STUDENT_NUMBER).roles("1234").attainments();

        studyRegistryService.getStudyAttainments("1234");
    }

    @Test(expected = NumberFormatException.class)
    public void thatSisuNativePersonIdThrowsExceptionWhenFetchingStudyAttainments() {
        studyRegistryService.getStudyAttainments("otm-sisu-native-id-here");
    }
}
