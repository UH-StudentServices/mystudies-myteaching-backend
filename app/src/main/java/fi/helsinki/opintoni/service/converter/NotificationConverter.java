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

import fi.helsinki.opintoni.domain.LocalizedText;
import fi.helsinki.opintoni.domain.Notification;
import fi.helsinki.opintoni.domain.NotificationSchedule;
import fi.helsinki.opintoni.dto.LocalizedNotificationDto;
import fi.helsinki.opintoni.dto.NotificationDto;
import fi.helsinki.opintoni.dto.NotificationScheduleDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class NotificationConverter {

    public LocalizedNotificationDto toDto(Notification notification, Locale locale) {
        LocalizedNotificationDto notificationDto = new LocalizedNotificationDto();
        notificationDto.id = notification.id;
        notificationDto.text = notification.text.getByLocale(locale);

        return notificationDto;
    }

    public NotificationDto toDto(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();

        notificationDto.text = notification.text.localizations;
        notificationDto.id = notification.id;
        notificationDto.schedules = notification.schedules.stream()
            .map(this::toNotificationScheduleDto)
            .sorted(Comparator.comparing(notificationScheduleDto -> notificationScheduleDto.startDate))
            .collect(Collectors.toList());

        return notificationDto;
    }

    public NotificationScheduleDto toNotificationScheduleDto(NotificationSchedule notificationSchedule) {
        NotificationScheduleDto notificationScheduleDto = new NotificationScheduleDto();

        notificationScheduleDto.endDate = notificationSchedule.endDate;
        notificationScheduleDto.startDate = notificationSchedule.startDate;
        notificationScheduleDto.id = notificationSchedule.id;

        return notificationScheduleDto;
    }

    public Notification fromDto(NotificationDto notificationDto) {
        Notification notification = new Notification();

        LocalizedText localizedText = new LocalizedText();
        localizedText.localizations = notificationDto.text;

        notification.text = localizedText;
        notification.schedules = notificationDto.schedules.stream()
            .map(schedule -> fromNotificationScheduleDto(schedule, notification)).collect(Collectors.toSet());

        return notification;
    }

    public NotificationSchedule fromNotificationScheduleDto(NotificationScheduleDto notificationScheduleDto, Notification notification) {
        NotificationSchedule notificationSchedule = new NotificationSchedule();
        notificationSchedule.notification = notification;
        notificationSchedule.startDate = notificationScheduleDto.startDate;
        notificationSchedule.endDate = notificationScheduleDto.endDate;

        return notificationSchedule;
    }
}
