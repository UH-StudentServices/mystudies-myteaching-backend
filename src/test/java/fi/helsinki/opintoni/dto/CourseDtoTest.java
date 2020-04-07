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

package fi.helsinki.opintoni.dto;

import com.google.common.collect.Lists;
import com.google.common.testing.EqualsTester;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class CourseDtoTest {

    @Test
    public void thatCoursesAreEqual() {
        CourseDto course1 = createCourseWithRealisationId("1");
        CourseDto course2 = createCourseWithRealisationId("1");

        new EqualsTester()
            .addEqualityGroup(course1, course2)
            .testEquals();
    }

    @Test
    public void thatCoursesAreNotEqual() {
        assertNotEquals(createCourseWithRealisationId("1"), createCourseWithRealisationId("2"));
    }

    private CourseDto createCourseWithRealisationId(String realisationId) {
        return new CourseDto(
            "1",
            1,
            "name",
            "",
            "",
            null,
            null,
            null,
            realisationId,
            null,
            null,
            4,
            Lists.newArrayList(),
            true,
            false,
            false,
            null);
    }
}
