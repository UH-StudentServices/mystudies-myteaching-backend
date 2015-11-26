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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminResourceTest extends SpringTest {

    @Test
    public void thatConfigurationPropertiesCanBeOverridden() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("url", "http://google.com");
        request.put("enabled", "false");

        mockMvc.perform(
            put("/api/private/v1/admin/configuration")
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk());
    }

    @Test
    public void thatConfigurationOverridingIsAllowedOnlyFromLocalhost() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("url", "http://google.com");
        request.put("enabled", "false");

        mockMvc.perform(
            put("/api/private/v1/admin/configuration")
                .with(r -> {
                    r.setRemoteAddr("123.123.123.123");
                    return r;
                })
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void thatConfigurationPropertyCanBeQueried() throws Exception {
        mockMvc.perform(
            get("/api/private/v1/admin/configuration?key=oodi.base.url")
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value("http://opi.helsinki.fi"));
    }

    @Test
    public void thatConfigurationQueryIsAllowedOnlyFromLocalhost() throws Exception {
        mockMvc.perform(
            get("/api/private/v1/admin/configuration?key=oodi.base.url")
                .with(r -> {
                    r.setRemoteAddr("123.123.123.123");
                    return r;
                })
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
