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
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateCalendarFeedResourceTest extends SpringTest {

    private final String CALENDAR_API_URL = "/api/private/v1/calendar";

    @Test
    public void thatCalendarFeedIsCreated() throws Exception {
        mockMvc.perform(post(CALENDAR_API_URL).with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedUrl").exists());
    }

    @Test
    public void thatCalendarFeedIsDeleted() throws Exception {
        mockMvc.perform(post(CALENDAR_API_URL).with(securityContext(teacherSecurityContext())));

        mockMvc.perform(delete(CALENDAR_API_URL).with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void thatCalendarFeedIsReturned() throws Exception {
        mockMvc.perform(get(CALENDAR_API_URL).with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedUrl")
                .value("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8"));
    }
}
