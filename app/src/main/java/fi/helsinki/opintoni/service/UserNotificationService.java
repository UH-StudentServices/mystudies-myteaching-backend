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

import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.dto.UserNotificationDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageNotification;
import fi.helsinki.opintoni.service.converter.UserNotificationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserNotificationService {

    private final CourseService courseService;
    private final TimeService timeService;
    private final CoursePageClient coursePageClient;
    private final UserNotificationConverter userNotificationConverter;
    private final UserNotificationTransactionalService userNotificationTransactionalService;

    @Autowired
    public UserNotificationService(CourseService courseService,
                                   TimeService timeService,
                                   CoursePageClient coursePageClient,
                                   UserNotificationConverter userNotificationConverter,
                                   UserNotificationTransactionalService userNotificationTransactionalService) {
        this.courseService = courseService;
        this.timeService = timeService;
        this.coursePageClient = coursePageClient;
        this.userNotificationConverter = userNotificationConverter;
        this.userNotificationTransactionalService = userNotificationTransactionalService;
    }

    public void insertNotificationIds(Long userId, List<String> notificationIds) {
        userNotificationTransactionalService.insertNotificationIds(userId, notificationIds);
    }

    public List<UserNotificationDto> getUserNotifications(Long userId,
                                                          Optional<String> studentNumber,
                                                          Optional<String> teacherNumber,
                                                          Locale locale) {
        Set<CourseDto> courseDtos = courseService.getCourses(studentNumber, teacherNumber, locale);
        Set<String> readNotificationIds = userNotificationTransactionalService.findReadNotificationIds(userId);

        return getUserNotificationDtos(courseDtos, readNotificationIds, locale)
            .stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
    }

    private List<UserNotificationDto> getUserNotificationDtos(final Set<CourseDto> courseDtos,
                                                              final Set<String> readNotificationIds,
                                                              final Locale locale) {
        Map<String, CourseDto> coursesByRealisationId = getCoursesByRealisationId(courseDtos);
        List<CoursePageNotification> notifications =
            coursePageClient.getCoursePageNotifications(coursesByRealisationId.keySet(), timeService.weekAgo(), locale);

        return notifications.stream()
            .map(coursePageNotification -> userNotificationConverter.toDto(
                coursesByRealisationId.get(coursePageNotification.courseImplementationId),
                coursePageNotification,
                readNotificationIds))
            .collect(Collectors.toList());
    }

    private Map<String, CourseDto> getCoursesByRealisationId(Set<CourseDto> courseDtos) {
        return courseDtos.stream().collect(Collectors.toMap(dto -> dto.realisationId, dto -> dto));
    }

}
