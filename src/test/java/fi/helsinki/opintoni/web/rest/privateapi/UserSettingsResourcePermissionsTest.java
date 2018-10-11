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
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserSettingsResourcePermissionsTest extends SpringTest {

    @Test
    public void thatUserCanOnlyUpdateHerSettings() throws Exception {
        UserSettingsDto userSettingsDto = new UserSettingsDto();

        mockMvc.perform(put("/api/private/v1/usersettings/1")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(userSettingsDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCanOnlyUpdateHerUserAvatar() throws Exception {
        UploadImageBase64Request uploadImageBase64Request = new UploadImageBase64Request();

        mockMvc.perform(put("/api/private/v1/usersettings/1/uploaduseravatar")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(uploadImageBase64Request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCanOnlyDeleteHerUserAvatar() throws Exception {
        mockMvc.perform(delete("/api/private/v1/usersettings/1/deleteuseravatar")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCanOnlyUploadHerBackground() throws Exception {
        UploadImageBase64Request request = new UploadImageBase64Request();

        mockMvc.perform(put("/api/private/v1/usersettings/1/uploadbackground")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCanOnlySelectHerBackground() throws Exception {
        SelectBackgroundRequest request = new SelectBackgroundRequest();

        mockMvc.perform(put("/api/private/v1/usersettings/1/selectbackground")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

}
