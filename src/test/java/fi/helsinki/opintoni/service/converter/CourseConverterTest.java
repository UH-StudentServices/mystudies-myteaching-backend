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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;

public class CourseConverterTest extends SpringTest {

    private static final String REALISATION_ID = "1";
    private static final String CODE = "10440";
    private static final String IMAGE_URI = "coursePageImageUrl";
    private static final Locale FI = new Locale("fi");

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
    public void thatTeacherCoursesAreEnrichedWithCourseCmsData() throws Exception {
        CourseDto dto = courseConverter.toDto(course(CODE, LocalDate.of(2020, 10, 26).atStartOfDay()), null, courseCMSPage(), FI);
        assertEnrichments(dto);
    }

    @Test
    public void thatTeacherCoursesAreEnrichedWithCoursePageData() throws Exception {
        CourseDto dto = courseConverter.toDto(course(CODE, LocalDate.of(2015, 10, 26).atStartOfDay()), coursePage(), null, FI);
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

    private void assertEnrichments(CourseDto dto) {
        assertThat(dto.imageUri, CoreMatchers.containsString(IMAGE_URI));
    }

}
