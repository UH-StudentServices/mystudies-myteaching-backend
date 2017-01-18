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
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OptimeCalendarResourceTest extends SpringTest {

    private final String API_URL = "/api/private/v1/optime/calendar";
    private final String TEACHER_NUMBER = "010540";

    private final String EXPECTED_CALENDAR_URL = "https://optime.example.com/IcalService/staff/99999";

    @Test
    public void thatCalendarUrlIsReturned() throws Exception {
        esbServer.expectTeacherCalendarRequest(TEACHER_NUMBER);

        mockMvc.perform(get(API_URL).with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value(EXPECTED_CALENDAR_URL));
    }

    @Test
    public void thatEmptyCalendarUrlIsReturned() throws Exception {
        esbServer.expectFailedTeacherCalendarRequest(TEACHER_NUMBER);

        mockMvc.perform(get(API_URL).with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").isEmpty());
    }
}
