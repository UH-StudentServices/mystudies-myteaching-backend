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

import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.dto.UserNotificationDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageNotification;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import fi.helsinki.opintoni.util.HtmlUtils;
import fi.helsinki.opintoni.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class UserNotificationConverter {

    private final CoursePageUriBuilder coursePageUriBuilder;
    private final UriBuilder uriBuilder;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserNotificationConverter(CoursePageUriBuilder coursePageUriBuilder,
                                     UriBuilder uriBuilder,
                                     UserRepository userRepository,
                                     UserSettingsRepository userSettingsRepository) {
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.uriBuilder = uriBuilder;
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserNotificationDto toDto(CourseDto courseDto,
                                     CoursePageNotification coursePageNotification,
                                     Set<String> readNotificationIds) {
        UserNotificationDto userNotificationDto = new UserNotificationDto();
        userNotificationDto.notificationId = coursePageNotification.id;
        userNotificationDto.user = coursePageNotification.user;
        userNotificationDto.message = HtmlUtils.getText(coursePageNotification.message);
        userNotificationDto.read = readNotificationIds.contains(coursePageNotification.id);
        userNotificationDto.timestamp = coursePageNotification.timestamp;
        userNotificationDto.courseName = courseDto.name;
        userNotificationDto.avatarUri = getAvatarUri(coursePageNotification.oodiPersonId);
        userNotificationDto.notificationUri = coursePageUriBuilder.getNotificationUriByNotificationType(
            courseDto.coursePageUri,
            coursePageNotification.type);
        return userNotificationDto;
    }

    private String getAvatarUri(String oodiPersonId) {
        if (oodiPersonId == null) {
            return uriBuilder.getDefaultUserAvatarUrl();
        }

        return userRepository.findByOodiPersonId(oodiPersonId)
            .map(user -> userSettingsRepository.findByUserId(user.id))
            .map(settings -> settings.hasAvatarImage()
                ? uriBuilder.getUserAvatarUrlByOodiPersonId(oodiPersonId)
                : uriBuilder.getDefaultUserAvatarUrl())
            .orElse(uriBuilder.getDefaultUserAvatarUrl());

    }

}
