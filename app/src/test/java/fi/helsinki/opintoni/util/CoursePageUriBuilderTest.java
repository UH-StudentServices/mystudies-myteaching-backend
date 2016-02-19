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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class CoursePageUriBuilderTest extends SpringTest {

    @Autowired
    private CoursePageUriBuilder coursePageUriBuilder;

    @Test
    public void thatEnglishUrisAreLocalized() {
        LocaleContextHolder.setLocale(new Locale("en"));

        CoursePageCourseImplementation coursePage = createCoursePage();
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isEqualTo("http://courses.helsinki.fi/123");
    }

    @Test
    public void thatFinnishUrisAreLocalized() {
        LocaleContextHolder.setLocale(new Locale("fi"));

        CoursePageCourseImplementation coursePage = createCoursePage();
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isEqualTo("http://courses.helsinki.fi/fi/123");
    }

    @Test
    public void thatSwedishUrisAreLocalized() {
        LocaleContextHolder.setLocale(new Locale("sv"));

        CoursePageCourseImplementation coursePage = createCoursePage();
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isEqualTo("http://courses.helsinki.fi/sv/123");
    }

    private CoursePageCourseImplementation createCoursePage() {
        CoursePageCourseImplementation coursePage = new CoursePageCourseImplementation();
        coursePage.url = "http://courses.helsinki.fi/123";
        return coursePage;
    }

    @Test
    public void thatEmptyCoursePageReturnsNullLocalizedUri() {
        CoursePageCourseImplementation coursePage = new CoursePageCourseImplementation();

        LocaleContextHolder.setLocale(new Locale("en"));
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isNull();

        LocaleContextHolder.setLocale(new Locale("fi"));
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isNull();

        LocaleContextHolder.setLocale(new Locale("sv"));
        assertThat(coursePageUriBuilder.getLocalizedUri(coursePage)).isNull();
    }

    @Test
    public void thatDefaultImageUriIsResolved() {
        String imageUri = coursePageUriBuilder.getImageUri(new CoursePageCourseImplementation());
        assertThat(imageUri).isEqualTo("https://dev.student.helsinki.fi/default");
    }
}
