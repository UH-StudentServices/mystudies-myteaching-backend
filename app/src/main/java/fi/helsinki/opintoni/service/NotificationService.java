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

import fi.helsinki.opintoni.domain.Notification;
import fi.helsinki.opintoni.domain.NotificationSchedule;
import fi.helsinki.opintoni.dto.LocalizedNotificationDto;
import fi.helsinki.opintoni.dto.NotificationDto;
import fi.helsinki.opintoni.dto.NotificationScheduleDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.NotificationRepository;
import fi.helsinki.opintoni.service.converter.NotificationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationConverter notificationConverter) {
        this.notificationRepository = notificationRepository;
        this.notificationConverter = notificationConverter;
    }

    public NotificationDto insertNotification(NotificationDto notificationDto) {
        return notificationConverter.toDto(
            notificationRepository.save(notificationConverter.fromDto(notificationDto)));
    }

    public NotificationDto insertSchedules(long notificationId, List<NotificationScheduleDto> notificationScheduleDtos) {
        Notification notification = findNotificationById(notificationId);

        List<NotificationSchedule> notificationSchedules = notificationScheduleDtos.stream()
            .map(notificationScheduleDto -> notificationConverter.fromNotificationScheduleDto(notificationScheduleDto, notification))
            .collect(Collectors.toList());

        notification.schedules.addAll(notificationSchedules);

        return notificationConverter.toDto(notificationRepository.save(notification));
    }

    public List<NotificationDto> getNotifications() {
        return notificationRepository.findAll().stream()
            .sorted(Comparator.comparing(notification -> notification.id))
            .map(notificationConverter::toDto).collect(Collectors.toList());
    }

    public NotificationDto getNotification(long notificationId) {
        return notificationConverter.toDto(findNotificationById(notificationId));
    }

    public void deleteNotification(long notificationId) {
        notificationRepository.delete(notificationId);
    }

    public NotificationDto deleteNotificationSchedule(long notificationId, long notificationScheduleId) {
        Notification notification = findNotificationById(notificationId);

        notification.schedules.removeIf(notificationSchedule -> notificationSchedule.id == notificationScheduleId);

        return notificationConverter.toDto(notificationRepository.save(notification));
    }

    public List<LocalizedNotificationDto> getNotifications(Locale locale) {
        return notificationRepository.findAll().stream()
            .filter(this::notificationShouldBeShown)
            .map(notification -> notificationConverter.toDto(notification, locale))
            .collect(Collectors.toList());
    }

    private boolean notificationShouldBeShown(Notification notification) {
        LocalDateTime nowDate = LocalDateTime.now();

        return notification.schedules.stream()
            .anyMatch(localDateTime -> nowDateIsWithinNotificationDateRange(localDateTime, nowDate));
    }

    private boolean nowDateIsWithinNotificationDateRange(NotificationSchedule schedule, LocalDateTime now) {
        return now.isEqual(schedule.startDate) || ( now.isAfter(schedule.startDate) && now.isBefore(schedule.endDate) ) || now.isEqual(schedule.endDate);
    }

    private Notification findNotificationById(long notificationId) {
        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotFoundException("Notification not found"));
    }
}
