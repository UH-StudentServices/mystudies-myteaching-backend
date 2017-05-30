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
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;

import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfficeHoursResourceTest extends SpringTest {

    private static final String NEW_OFFICE_HOURS = "uudet ajat";

    @Test
    public void thatOfficeHoursReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.description").value(OFFICE_HOURS));
    }

    @Test
    public void thatOfficeHoursAreUpdated() throws Exception {
        OfficeHoursDto request = new OfficeHoursDto();
        request.description = NEW_OFFICE_HOURS;

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.description").value(NEW_OFFICE_HOURS));
    }

    @Test
    public void thatOfficeHoursAreDeleted() throws Exception {
        mockMvc.perform(delete("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.description").doesNotExist());
    }

}
