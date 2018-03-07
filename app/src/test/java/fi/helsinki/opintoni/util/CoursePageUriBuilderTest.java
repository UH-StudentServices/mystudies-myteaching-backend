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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class CoursePageUriBuilderTest extends SpringTest {

    private static final String MATERIAL_URI_SUFFIX = "?group-imp-material";

    @Autowired
    private CoursePageUriBuilder coursePageUriBuilder;

    private static final String COURSE_PAGE_URL = "https://dev.courses.helsinki.fi/en/filk-111/120463247";

    @Test
    public void thatDefaultImageUriIsResolved() {
        String imageUri = coursePageUriBuilder.getImageUri(new CoursePageCourseImplementation());
        assertThat(imageUri).isEqualTo("https://dev.student.helsinki.fi/default");
    }

    @Test
    public void thatMaterialUriIsResolved() {

        CoursePageCourseImplementation coursePageCourseImplementation = new CoursePageCourseImplementation();
        coursePageCourseImplementation.url = COURSE_PAGE_URL;
        String materialUri = coursePageUriBuilder.getMaterialUri(coursePageCourseImplementation);
        assertEquals(materialUri, COURSE_PAGE_URL + MATERIAL_URI_SUFFIX);
    }

    @Test
    public void thatNullMaterialUriIsReturnedIfCoursePageUrlIsNull() {
        String materialUri = coursePageUriBuilder.getMaterialUri(new CoursePageCourseImplementation());
        assertThat(materialUri).isNull();
    }
}
