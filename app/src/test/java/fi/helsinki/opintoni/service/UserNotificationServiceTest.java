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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.UserNotificationDto;
import fi.helsinki.opintoni.web.TestConstants;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserNotificationServiceTest extends SpringTest {

    @Autowired
    private UserNotificationService userNotificationService;

    @Test
    public void thatUserNotificationsAreReturned() {
        defaultTeacherRequestChain()
            .courses()
            .defaultCourseImplementation()
            .and()
            .examCourseImplementation()
            .and()
            .activity("teachernotifications.json");

        List<UserNotificationDto> userNotifications = userNotificationService
            .getUserNotifications(1L, Optional.empty(), Optional.of(TestConstants.TEACHER_NUMBER), Locale.ENGLISH);

        assertEquals(2, userNotifications.size());

        UserNotificationDto userNotificationDto = userNotifications.get(0);
        assertEquals("3", userNotificationDto.notificationId);
        assertEquals("has written a message", userNotificationDto.message);
        assertTrue(userNotificationDto.read);

        userNotificationDto = userNotifications.get(1);
        assertEquals("4", userNotificationDto.notificationId);
        assertEquals("has removed an event", userNotificationDto.message);
        assertFalse(userNotificationDto.read);
    }

    public static class ActivityUrlMatcher extends TypeSafeMatcher<String> {

        private final String courseRealisationId;
        private final String urlTemplate;

        public ActivityUrlMatcher(String coursePageBaseUrl, String courseRealisationId) {
            this.courseRealisationId = courseRealisationId;
            this.urlTemplate = coursePageBaseUrl + "/course_implementation_activity" +
                "?course_implementation_id=%s&timestamp=";
        }

        @Override
        public boolean matchesSafely(String url) {
            return StringUtils.contains(
                url,
                String.format(urlTemplate, courseRealisationId));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("url containing " + urlTemplate, courseRealisationId));
        }
    }
}
