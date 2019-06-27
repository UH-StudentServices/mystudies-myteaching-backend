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

package fi.helsinki.opintoni.web.rest.adminapi;

import fi.helsinki.opintoni.dto.NotificationDto;
import fi.helsinki.opintoni.dto.NotificationScheduleDto;
import fi.helsinki.opintoni.service.NotificationService;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.ADMIN_API_V1 + "/notifications")
public class NotificationResource extends AbstractResource {
    private final NotificationService notificationService;

    @Autowired
    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RequestMapping(
        method = RequestMethod.POST)
    public ResponseEntity<NotificationDto> insertNotification(
        @Valid @RequestBody NotificationDto notificationDto) {
        return response(notificationService.insertNotification(notificationDto));
    }

    @RequestMapping(
        method = RequestMethod.GET)
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        return response(notificationService.getNotifications());
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/{id}")
    public ResponseEntity<NotificationDto> getNotification(@PathVariable("id") long notificationId) {
        return response(notificationService.getNotification(notificationId));
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(
        @PathVariable("id") long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{id}/schedules/{scheduleId}")
    public ResponseEntity<NotificationDto> deleteNotification(
        @PathVariable("id") long notificationId,
        @PathVariable("scheduleId") long scheduleId) {
        return response(notificationService.deleteNotificationSchedule(notificationId, scheduleId));
    }

    @RequestMapping(
        method = RequestMethod.POST,
        value = "/{id}/schedules")
    public ResponseEntity<NotificationDto> insertSchedule(
        @PathVariable("id") long notificationId,
        @Valid @RequestBody List<NotificationScheduleDto> notificationScheduleDto) {
        return response(notificationService.insertSchedules(notificationId, notificationScheduleDto));
    }
}
