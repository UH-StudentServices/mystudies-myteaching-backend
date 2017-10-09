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

package fi.helsinki.opintoni.integration.coursepage;

import com.google.common.collect.Sets;
import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CoursePageRestClientTest extends SpringTest {

    @Autowired
    private CoursePageClient coursePageRestClient;

    @Test
    public void thatImageUriIsReturned() {
        defaultTeacherRequestChain().defaultCourseImplementation();

        CoursePageCourseImplementation coursePage = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID);

        assertThat(coursePage.imageUrl).isEqualTo("http://dev.student.helsinki.fi/image");
    }

    @Test
    public void thatEmptyImageUriIsReturnedWhenCoursePageHasNoImageUri() {
        defaultTeacherRequestChain()
            .courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_without_image.json");

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID).imageUrl).isEqualTo("");
    }

    @Test
    public void thatNullImageUriIsReturnedWhenCoursePageDoesNotExist() {
        defaultTeacherRequestChain().courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_empty.json");

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID).imageUrl).isNull();
    }

    @Test
    public void thatEmptyNotificationListIsReturnedWithEmptyIds() {
        List<CoursePageNotification> notifications = coursePageRestClient
            .getCoursePageNotifications(Sets.newHashSet(), LocalDateTime.now(), Locale.ENGLISH);
        assertThat(notifications.isEmpty()).isTrue();
    }

    @Test
    public void thatMoodleUrlIsReturned() {
        defaultTeacherRequestChain()
            .courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_with_moodle_url.json");

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID).moodleUrl).isEqualTo("http://moodle.helsinki.fi");
    }

    @Test
    public void thatCourseImplementationsAreCached() {
        defaultTeacherRequestChain().defaultCourseImplementation();

        CoursePageCourseImplementation implementations = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID);
        CoursePageCourseImplementation cachedImplementations =
            coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID);

        assertThat(implementations).isSameAs(cachedImplementations);
    }
}
