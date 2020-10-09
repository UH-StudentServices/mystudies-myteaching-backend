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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsFile;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsIntegrationException;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.sotka.model.SotkaHierarchy;
import fi.helsinki.opintoni.integration.studyregistry.Organisation;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CourseServiceTest extends SpringTest {

    private static final String TEACHER_ID = "1";
    private static final String CODE = "10440";
    private static final String ORG_OPEN_UNIVERSITY = "hy-org-48645785";
    private static final String ORG = "hy-org-48645786";
    private static final String IMAGE_URI = "coursePageImageUrl";
    private static final String SISU_REALISATION_FROM_OPTIME_ID = "hy-opt-cur-2021-b33fa3e8-c4a5-4b71-894d-7f4a3639e911";
    private static final String SISU_REALISATION_FROM_OPTIME_ID2 = "hy-opt-cur-2022-b33fa3e8-c4a5-4b71-894d-7f4a3639e911";
    private static final String OODI_ID = "1234567";
    private static final String OODI_ID2 = "2345678";
    private static final Locale FI = new Locale("fi");

    @Autowired
    CourseService courseService;

    @MockBean
    CourseCmsClient mockCourseCmsClient;

    @MockBean
    CoursePageClient mockCoursePageClient;

    @MockBean
    SotkaClient mockSotkaClient;

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    @Test
    public void thatOldCoursepageisFetchedforCoursesBeforeCutoffDate() throws Exception {
        mockStudyRegistry(ORG, LocalDate.of(2020, 5, 10).atStartOfDay());
        mockSokcaClient();
        Mockito.when(mockCoursePageClient.getCoursePage(anyString(), Mockito.any(Locale.class))).thenReturn(coursePage());

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        assertCoursePageCalled(OODI_ID, OODI_ID2);
        verify(mockSotkaClient, times(1)).getOptimeHierarchies(List.of(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID));
    }

    @Test
    public void thatNewCoursepageIsFetchedforCoursesAfterCutoffDate() throws Exception {
        mockStudyRegistry(ORG, LocalDate.of(2020, 9, 10).atStartOfDay());
        Mockito.when(mockCourseCmsClient.getCoursePage(anyString(), Mockito.any(Locale.class))).thenReturn(cmsPage());

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        assertCMSCalled(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID);
    }

    @Test
    public void thatOldCoursePageIsFetchedForOpenUniversityCoursesAfterCutOffDate() throws Exception {
        mockStudyRegistry(ORG_OPEN_UNIVERSITY, LocalDate.of(2020, 9, 10).atStartOfDay());
        mockSokcaClient();
        Mockito.when(mockCoursePageClient.getCoursePage(anyString(), Mockito.any(Locale.class))).thenReturn(coursePage());

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        assertCoursePageCalled(OODI_ID, OODI_ID2);
    }

    @Test
    public void thatCoursesAreReturnedWhenCMSCallFails() throws Exception {
        mockStudyRegistry(ORG, LocalDate.of(2020, 9, 10).atStartOfDay());
        Mockito.when(mockCourseCmsClient.getCoursePage(anyString(), Mockito.any(Locale.class))).thenThrow(CourseCmsIntegrationException.class);

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        assertCMSCalled(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID);
    }

    @Test
    public void thatCoursesAreReturnedWhenSotkaCallFails() throws Exception {
        mockStudyRegistry(ORG, LocalDate.of(2020, 5, 10).atStartOfDay());
        Mockito.when(mockSotkaClient.getOptimeHierarchies(
            List.of(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID))).thenReturn(List.of());

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        verify(mockSotkaClient, times(1)).getOptimeHierarchies(List.of(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID));
    }

    @Test
    public void thatCoursesAreReturnedWhenCoursePageCallFails() throws Exception {
        mockStudyRegistry(ORG, LocalDate.of(2020, 5, 10).atStartOfDay());
        mockSokcaClient();
        Mockito.when(mockCoursePageClient.getCoursePage(anyString(), Mockito.any(Locale.class))).thenThrow(new RuntimeException("no internet"));

        List<CourseDto> dtos = courseService.getTeacherCourses(TEACHER_ID, FI);

        assertEquals(2, dtos.size());
        assertCoursePageCalled(OODI_ID, OODI_ID2);
    }

    private void assertCMSCalled(String... ids) {
        List.of(ids).forEach(id -> verify(mockCourseCmsClient, times(1)).getCoursePage(id, FI));
    }

    private void assertCoursePageCalled(String... ids) {
        List.of(ids).forEach(id -> verify(mockCoursePageClient, times(1)).getCoursePage(id, FI));
    }

    private CoursePageCourseImplementation coursePage() {
        CoursePageCourseImplementation coursePage = new CoursePageCourseImplementation();
        coursePage.imageUrl = IMAGE_URI;
        return coursePage;
    }

    private CourseCmsCourseUnitRealisation cmsPage() {
        CourseCmsCourseUnitRealisation coursePage = new CourseCmsCourseUnitRealisation();
        coursePage.courseImage = new CourseCmsFile();
        return coursePage;
    }

    private void mockStudyRegistry(String org, LocalDateTime start) {
        Mockito.when(mockSisuStudyRegistry.getTeacherCourses(Mockito.anyString(), Mockito.any(LocalDate.class))).thenReturn(
            List.of(
                course(org, SISU_REALISATION_FROM_OPTIME_ID2, start),
                course(org, SISU_REALISATION_FROM_OPTIME_ID, start)
            )
        );
    }

    private void mockSokcaClient() {
        Mockito.when(mockSotkaClient.getOptimeHierarchies(
            List.of(SISU_REALISATION_FROM_OPTIME_ID2, SISU_REALISATION_FROM_OPTIME_ID))).thenReturn(List.of(
            sotkaHierarchy(OODI_ID, SISU_REALISATION_FROM_OPTIME_ID),
            sotkaHierarchy(OODI_ID2, SISU_REALISATION_FROM_OPTIME_ID2)
        ));
    }

    private TeacherCourse course(String org, String realisationId, LocalDateTime startDate) {
        TeacherCourse course = new TeacherCourse();
        course.learningOpportunityId = CODE;
        course.realisationId = realisationId;
        course.rootId = realisationId;
        course.startDate = startDate;
        course.organisations = List.of(new Organisation(org, List.of()));
        course.endDate = startDate.plusMonths(2);
        return course;
    }

    private SotkaHierarchy sotkaHierarchy(String oodiId, String sisuRealisationFromOptimeId) {
        SotkaHierarchy sotkaHierarchy = new SotkaHierarchy();
        sotkaHierarchy.oodiId = oodiId;
        sotkaHierarchy.optimeId = sisuRealisationFromOptimeId;
        return sotkaHierarchy;
    }

}
