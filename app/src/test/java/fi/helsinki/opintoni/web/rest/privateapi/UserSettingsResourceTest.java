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
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.integration.mece.JWTService;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.service.storage.FilesMemory;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UpdateUserSettingsRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.sampledata.SampleDataFiles.imageToBase64;
import static fi.helsinki.opintoni.sampledata.UserSettingsSampleData.BACKGROUND_URI;
import static fi.helsinki.opintoni.sampledata.UserSettingsSampleData.USER_SETTINGS_ID;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserSettingsResourceTest extends SpringTest {

    private static final String STUDENT_PRINCIPAL_NAME = "opiskelija@helsinki.fi";
    @Autowired
    private FilesMemory filesMemory;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private JWTService jwtService;

    @Test
    public void thatUserSettingsReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/private/v1/usersettings")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(USER_SETTINGS_ID.intValue()))
            .andExpect(jsonPath("$.backgroundUri").value(BACKGROUND_URI))
            .andExpect(jsonPath("$.backgroundType").value("DEFAULT"))
            .andExpect(jsonPath("$.showBanner").value(true))
            .andExpect(jsonPath("$.cookieConsent").value(false))
            .andExpect(jsonPath("$.meceJWTToken").value(jwtService.generateToken(STUDENT_PRINCIPAL_NAME)));
    }

    @Test
    public void thatUserSettingsAreUpdated() throws Exception {
        UpdateUserSettingsRequest request = new UpdateUserSettingsRequest();
        request.showBanner = false;
        request.cookieConsent = true;

        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID)
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(USER_SETTINGS_ID.intValue()))
            .andExpect(jsonPath("$.backgroundType").value("DEFAULT"))
            .andExpect(jsonPath("$.showBanner").value(false))
            .andExpect(jsonPath("$.cookieConsent").value(true));
    }

    @Test
    public void thatUserAvatarIsUpdated() throws Exception {
        UploadImageBase64Request request = new UploadImageBase64Request();
        request.imageBase64 = imageToBase64("usersettings/useravatar.jpg");

        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/uploaduseravatar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk());
    }

    @Test
    public void thatUserAvatarIsDeleted() throws Exception {
        mockMvc.perform(delete("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/deleteuseravatar")
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk());
    }

    @Test
    public void thatBackgroundIsUpdated() throws Exception {
        UploadImageBase64Request request = new UploadImageBase64Request();
        request.imageBase64 = imageToBase64("usersettings/useravatar.jpg");

        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/uploadbackground")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri", notNullValue()))
            .andExpect(jsonPath("$.backgroundType").value("CUSTOM"));
    }


    @Test
    public void thatBackgroundIsSelected() throws Exception {
        SelectBackgroundRequest request = new SelectBackgroundRequest();
        request.filename = "Profile_1.jpg";

        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/selectbackground")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(
                "https://opi-1.student.helsinki.fi/api/public/v1/images/backgrounds/Profile_1.jpg"
            ))
            .andExpect(jsonPath("$.backgroundType").value("DEFAULT"));
    }

    @Test
    public void thatOldUploadedBackgroundIsRemovedWhenBackgroundIsSelected() throws Exception {
        String uploadedBackgroundFilename = "oldpicture.jpg";

        addUploadedBackgroundToDbAndFileSystem(uploadedBackgroundFilename);
        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isTrue();

        SelectBackgroundRequest request = new SelectBackgroundRequest();
        request.filename = "Profile_1.jpg";
        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/selectbackground")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON));

        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isFalse();
    }

    @Test
    public void thatOldUploadedBackgroundIsRemovedWhenBackgroundIsUploaded() throws Exception {
        String uploadedBackgroundFilename = "oldpicture.jpg";

        addUploadedBackgroundToDbAndFileSystem(uploadedBackgroundFilename);
        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isTrue();

        UploadImageBase64Request request = new UploadImageBase64Request();
        request.imageBase64 = imageToBase64("usersettings/useravatar.jpg");

        mockMvc.perform(put("/api/private/v1/usersettings/" + USER_SETTINGS_ID + "/uploadbackground")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isFalse();
    }

    private void addUploadedBackgroundToDbAndFileSystem(String uploadedBackgroundFilename) {
        filesMemory.put(uploadedBackgroundFilename, new byte[0]);
        UserSettings userSettings = userSettingsRepository.findOne(USER_SETTINGS_ID);
        userSettings.uploadedBackgroundFilename = uploadedBackgroundFilename;
        userSettingsRepository.save(userSettings);
    }
}
