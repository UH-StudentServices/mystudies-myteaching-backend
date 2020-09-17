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

package fi.helsinki.opintoni.service.converter;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsFile;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.sotka.OodiHierarchy;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.Organisation;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.util.CoursePageUtil;

public class CourseConverterTest extends SpringTest {

    private static final String REALISATION_ID = "1";
    private static final String CODE = "10440";
    private static final String IMAGE_URI = "coursePageImageUrl";
    private static final String SISU_REALISATION_FROM_OPTIME_ID = "hy-opt-cur-2021-b33fa3e8-c4a5-4b71-894d-7f4a3639e911";
    private static final String OODI_ID = "1234567";

    @Autowired
    CourseConverter courseConverter;

    @MockBean
    CourseCmsClient mockCourseCmsClient;

    @MockBean
    CoursePageClient mockCoursePageClient;

    @MockBean
    SotkaClient mockSotkaClient;

    private TeacherCourse course(String code, LocalDateTime startDate) {
        TeacherCourse course = new TeacherCourse();
        course.learningOpportunityId = code;
        course.realisationId = REALISATION_ID;
        course.rootId = REALISATION_ID;
        course.startDate = startDate;
        course.endDate = LocalDate.of(2016, 4, 27).atStartOfDay();
        return course;
    }

    private CoursePageCourseImplementation coursePage() {
        CoursePageCourseImplementation coursePage = new CoursePageCourseImplementation();
        coursePage.imageUrl = IMAGE_URI;
        return coursePage;
    }

    @Test
    public void thatTeacherCoursesAreEnrichedWithCourseCmsDataForCoursesStartingAfterCutOffDate() throws Exception {
        when(mockCourseCmsClient.getCoursePage(anyString(), any(Locale.class))).thenReturn(courseCMSPage());
        CourseDto dto = courseConverter.toDto(course(CODE, LocalDate.of(2020, 10, 26).atStartOfDay()), new Locale("fi"),
                true);
        assertCMSCalled();
        assertEnrichments(dto);
    }

    @Test
    public void thatTeacherCoursesAreEnrichedWithCoursePageDataForCoursesStartingBeforeCutOffDate() throws Exception {
        when(mockCoursePageClient.getCoursePage(anyString(), any(Locale.class))).thenReturn(coursePage());
        CourseDto dto = courseConverter.toDto(course(CODE, LocalDate.of(2015, 10, 26).atStartOfDay()), new Locale("fi"), true);
        assertCoursePageCalled();
        assertEnrichments(dto);
    }

    @Test
    public void thatTeacherCoursesForOpenUniversityStartingAfterCutOffDateAreEnrichedWithOldCoursePageData() throws Exception {
        when(mockCoursePageClient.getCoursePage(anyString(), any(Locale.class))).thenReturn(coursePage());
        TeacherCourse course = course(CODE, LocalDate.of(2020, 10, 26).atStartOfDay());
        course.organisations = List.of(new Organisation(CoursePageUtil.OPEN_UNIVERSITY_ORG_CODE, List.of()));
        CourseDto dto = courseConverter.toDto(course, new Locale("fi"), true);
        assertEnrichments(dto);
        assertCoursePageCalled();
    }

    @Test
    public void thatTeacherCourseCourseCmsDataIsFetchedWithOodiIdIfSotkaDataIsNotFoundForRealisation() throws Exception {
        TeacherCourse course = course(CODE, LocalDate.of(2019, 10, 26).atStartOfDay());
        course.realisationId = SISU_REALISATION_FROM_OPTIME_ID;

        OodiHierarchy oodiHierarchy = new OodiHierarchy();
        oodiHierarchy.oodiId = OODI_ID;

        when(mockSotkaClient.getOodiHierarchy(SISU_REALISATION_FROM_OPTIME_ID)).thenReturn(oodiHierarchy);
        when(mockCoursePageClient.getCoursePage(eq(OODI_ID), any(Locale.class))).thenReturn(coursePage());

        CourseDto dto = courseConverter.toDto(course, new Locale("fi"), true);

        verify(mockCoursePageClient, times(1)).getCoursePage(OODI_ID, new Locale("fi"));
        verify(mockSotkaClient, times(1)).getOodiHierarchy(SISU_REALISATION_FROM_OPTIME_ID);

        assertEnrichments(dto);
    }

    private CourseCmsCourseUnitRealisation courseCMSPage() {
        CourseCmsCourseUnitRealisation coursePage = new CourseCmsCourseUnitRealisation();
        CourseCmsFile file = new CourseCmsFile();
        file.uri = new CourseCmsFile.Uri();
        file.uri.url = IMAGE_URI;
        coursePage.courseImage = file;
        return coursePage;
    }

    private void assertCMSCalled() {
        verify(mockCourseCmsClient, times(1)).getCoursePage(REALISATION_ID, new Locale("fi"));
        verify(mockCoursePageClient, times(0)).getCoursePage(REALISATION_ID, new Locale("fi"));
    }

    private void assertCoursePageCalled() {
        verify(mockCourseCmsClient, times(0)).getCoursePage(REALISATION_ID, new Locale("fi"));
        verify(mockCoursePageClient, times(1)).getCoursePage(REALISATION_ID, new Locale("fi"));
    }

    private void assertEnrichments(CourseDto dto) {
        assertThat(dto.imageUri, CoreMatchers.containsString(IMAGE_URI));
    }

}
