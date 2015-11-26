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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserNotificationsResourceTest extends SpringTest {

    @Test
    public void thatInsertReadUserNotificationsReturnCorrectResponse() throws Exception {
        insertNotifications().andExpect(status().isOk());
    }

    private ResultActions insertNotifications() throws Exception {
        return mockMvc.perform(post("/api/private/v1/usernotifications").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(Arrays.asList("abc", "123", "cde", "456")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void thatUserNotificationsAreReturned() throws Exception {
        expectNotifications();

        mockMvc.perform(get("/api/private/v1/usernotifications").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].notificationId").value("3"))
            .andExpect(jsonPath("$[0].courseName").value("Geenitekniikka"))
            .andExpect(jsonPath("$[0].notificationUri")
                .value("https://dev.student.helsinki.fi/tvt?group-course-messages"))
            .andExpect(jsonPath("$[0].avatarUri")
                .value("https://opi-1.student.helsinki.fi/app/assets/icons/avatar.png"))
            .andExpect(jsonPath("$[0].message").value("has written a message"))
            .andExpect(jsonPath("$[0].user").value("admin"))
            .andExpect(jsonPath("$[0].read").value(false))
            .andExpect(jsonPath("$[0].timestamp").isArray())
            .andExpect(jsonPath("$[0].timestamp[0]").value(2015))
            .andExpect(jsonPath("$[0].timestamp[1]").value(6))
            .andExpect(jsonPath("$[0].timestamp[2]").value(5))
            .andExpect(jsonPath("$[0].timestamp[3]").value(11))
            .andExpect(jsonPath("$[0].timestamp[4]").value(8))
            .andExpect(jsonPath("$[0].timestamp[5]").value(6));
    }

    private void expectNotifications() {
        defaultStudentRequestChain()
            .enrollments()
            .defaultCourseUnitRealisation()
            .defaultImplementation()
            .activity("studentnotifications.json");
    }

}
