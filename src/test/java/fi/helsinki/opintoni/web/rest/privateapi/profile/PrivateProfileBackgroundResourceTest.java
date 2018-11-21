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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.profile.ProfileBackground;
import fi.helsinki.opintoni.repository.profile.ProfileBackgroundRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.storage.FilesMemory;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;

import static fi.helsinki.opintoni.sampledata.SampleDataFiles.imageToBase64;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateProfileBackgroundResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/profile/2/background";
    private static final long PROFILE_ID = 2L;

    @Autowired
    private FilesMemory filesMemory;

    @Autowired
    private ProfileBackgroundRepository profileBackgroundRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    public void thatBackgroundUriCanBeGet() throws Exception {
        mockMvc.perform(get(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(containsString("Profile_")));
    }

    @Test
    public void thatBackgroundCanBeSelectedFromDefaults() throws Exception {
        SelectBackgroundRequest request = new SelectBackgroundRequest();
        request.filename = "Profile_1.jpg";

        mockMvc.perform(put(RESOURCE_URL + "/select")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Optional<ProfileBackground> background = profileBackgroundRepository.findByProfileId(PROFILE_ID);

        Assertions.assertThat(background.isPresent()).isTrue();
        Assertions.assertThat(background.get().backgroundFilename).isEqualTo(request.filename);
    }

    @Test
    public void thatOldBackgroundFileIsDeleted() throws Exception {
        String uploadedBackgroundFilename = "oldpicture.jpg";

        addUploadedBackgroundToDbAndFileSystem(uploadedBackgroundFilename);
        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isTrue();

        SelectBackgroundRequest request = new SelectBackgroundRequest();
        request.filename = "Profile_1.jpg";

        mockMvc.perform(put(RESOURCE_URL + "/select")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isFalse();
    }

    @Test
    public void thatBackgroundCanBeUploaded() throws Exception {
        String uploadedBackgroundFilename = "oldpicture.jpg";

        addUploadedBackgroundToDbAndFileSystem(uploadedBackgroundFilename);
        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isTrue();

        UploadImageBase64Request request = new UploadImageBase64Request();
        request.imageBase64 = imageToBase64("usersettings/useravatar.jpg");

        mockMvc.perform(put(RESOURCE_URL + "/upload")
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Optional<ProfileBackground> background = profileBackgroundRepository.findByProfileId(PROFILE_ID);

        Assertions.assertThat(background.isPresent()).isTrue();
        Assertions.assertThat(background.get().backgroundFilename).isNotBlank();

        assertThat(filesMemory.contains(uploadedBackgroundFilename)).isFalse();
    }

    private void addUploadedBackgroundToDbAndFileSystem(String uploadedBackgroundFilename) {
        ProfileBackground background = new ProfileBackground();

        filesMemory.put(uploadedBackgroundFilename, new byte[0]);
        background.uploadedBackgroundFilename = uploadedBackgroundFilename;
        background.profile = profileRepository.findById(PROFILE_ID).get();
        profileBackgroundRepository.save(background);
    }
}
