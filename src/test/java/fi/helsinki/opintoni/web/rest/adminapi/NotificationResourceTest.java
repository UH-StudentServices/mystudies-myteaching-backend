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

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.NotificationDto;
import fi.helsinki.opintoni.dto.NotificationScheduleDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.NotificationRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.assertLocalDateTimeJsonArray;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationResourceTest extends SpringTest {
    @Autowired
    private NotificationRepository notificationRepository;

    private static final String NOTIFICATION_RESOURCE_PATH = RestConstants.ADMIN_API_V1 + "/notifications";

    private static final String NOTIFICATION_1_TEXT_FI = "Huoltokatkoilmoitus 1 (fi)";
    private static final String NOTIFICATION_2_TEXT_FI = "Huoltokatkoilmoitus 2 (fi)";
    private static final String NOTIFICATION_3_TEXT_FI = "Huoltokatkoilmoitus 3 (fi)";

    private NotificationDto createNotification() {
        NotificationScheduleDto notificationSchedule = new NotificationScheduleDto();

        notificationSchedule.startDate = LocalDateTime.of(2017, 1, 1, 12, 0, 0, 0);
        notificationSchedule.endDate = notificationSchedule.startDate.plusDays(2);

        NotificationDto notification = new NotificationDto();
        notification.schedules.add(notificationSchedule);

        notification.text = ImmutableMap.of(Language.FI, "Huoltokatko", Language.EN, "Huoltokatko (en)", Language.SV, "Huoltokatko (sv)");

        return notification;
    }

    private void assertInsertOrUpdateNotificationResult(ResultActions result, NotificationDto notificationDto) throws Exception {
        result
            .andExpect(jsonPath("$.text.fi")
                .value(notificationDto.text.get(Language.FI)))
            .andExpect(jsonPath("$.text.sv")
                .value(notificationDto.text.get(Language.SV)))
            .andExpect(jsonPath("$.text.en")
                .value(notificationDto.text.get(Language.EN)))
            .andExpect(jsonPath("$.schedules", hasSize(notificationDto.schedules.size())));

        IntStream
            .range(0, notificationDto.schedules.size() - 1)
            .forEach(i -> assertNotificationScheduleDates(result, notificationDto.schedules.get(i), i));
    }

    private void assertNotificationScheduleDates(ResultActions result, NotificationScheduleDto notificationScheduleDto, int index) {
        try {
            assertLocalDateTimeJsonArray(result, String.format("$.schedules[%s].startDate", index), notificationScheduleDto.startDate);
            assertLocalDateTimeJsonArray(result, String.format("$.schedules[%s].endDate", index), notificationScheduleDto.endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error when asserting notification schedule dates");
        }
    }

    private ResultActions postNewNotification(NotificationDto notification) throws Exception {
        return mockMvc.perform(post(NOTIFICATION_RESOURCE_PATH)
            .with(securityContext(teacherSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(notification))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void thatAllNotificationsAreRetrieved() throws Exception {
        mockMvc.perform(get(NOTIFICATION_RESOURCE_PATH)
            .with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].text.fi").value(NOTIFICATION_1_TEXT_FI))
            .andExpect(jsonPath("$[1].text.fi").value(NOTIFICATION_2_TEXT_FI))
            .andExpect(jsonPath("$[2].text.fi").value(NOTIFICATION_3_TEXT_FI));
    }

    @Test
    public void thatNotificationIsRetrievedById() throws Exception {
        mockMvc.perform(get(NOTIFICATION_RESOURCE_PATH + "/1")
            .with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text.fi").value(NOTIFICATION_1_TEXT_FI));
    }

    @Test
    public void thatNotificationIsInserted() throws Exception {
        NotificationDto notification = createNotification();

        ResultActions result = postNewNotification(notification);

        assertInsertOrUpdateNotificationResult(result, notification);
    }

    @Test
    public void thatNewScheduleIsInsertedToNotification() throws Exception {
        NotificationDto notification = createNotification();

        final long id = extractNotificationIdFromResponse(postNewNotification(notification).andReturn().getResponse().getContentAsString());

        NotificationScheduleDto notificationSchedule = new NotificationScheduleDto();

        notificationSchedule.startDate = LocalDateTime.of(2018, 1, 1, 12, 0, 0, 0);
        notificationSchedule.endDate = notificationSchedule.startDate.plusDays(2);

        notification.schedules.add(notificationSchedule);

        ResultActions result = mockMvc.perform(post(NOTIFICATION_RESOURCE_PATH + String.format("/%s/schedules", id))
            .with(securityContext(teacherSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(asList(notificationSchedule)))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertInsertOrUpdateNotificationResult(result, notification);
    }

    @Test
    public void thatNotificationIsDeleted() throws Exception {
        NotificationDto notification = createNotification();

        long id = extractNotificationIdFromResponse(postNewNotification(notification).andReturn().getResponse().getContentAsString());

        assertNotNull(notificationRepository.findById(id).get());

        mockMvc.perform(delete(NOTIFICATION_RESOURCE_PATH + String.format("/%s", id))
            .with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertNull(notificationRepository.findById(id).orElse(null));
    }

    @Test
    public void thatScheduleIsDeleted() throws Exception {
        NotificationDto notification = createNotification();

        String responseJson = postNewNotification(notification).andReturn().getResponse().getContentAsString();

        int scheduleIndexToRemove = 0;

        long notificationId = extractNotificationIdFromResponse(responseJson);
        long scheduleIdToDelete = extractScheduleIdIdFromResponse(responseJson, scheduleIndexToRemove);

        notification.schedules.remove(scheduleIndexToRemove);

        ResultActions deleteScheduleResult = mockMvc.perform(
            delete(NOTIFICATION_RESOURCE_PATH + String.format("/%s/schedules/%s", notificationId, scheduleIdToDelete))
            .with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertInsertOrUpdateNotificationResult(deleteScheduleResult, notification);
    }

    @Test
    public void thatNonAdminUserIsNotAuthorizedToManageNotifications() throws Exception {
        mockMvc.perform(post(NOTIFICATION_RESOURCE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    private Long extractNotificationIdFromResponse(String responseJson) {
        return extractValueFromResponse(responseJson, "$.id", Long.class);
    }

    private Long extractScheduleIdIdFromResponse(String responseJson, int index) {
        return extractValueFromResponse(responseJson, String.format("$.schedules[%s].id", index), Long.class);
    }

    private <T> T extractValueFromResponse(String responseJson, String path, Class<T> type) {
        return JsonPath.parse(responseJson).read(path, type);
    }
}
