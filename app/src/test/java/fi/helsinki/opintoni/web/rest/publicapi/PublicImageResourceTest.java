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

package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PublicImageResourceTest extends SpringTest {

    @Autowired
    private UserSettingsService userSettingsService;

    @Before
    public void insertUserAvatar() throws IOException {
        userSettingsService.updateUserAvatar(3L, getImageData());
    }

    @Test
    public void thatUserAvatarIsReturned() throws Exception {
        mockMvc.perform(get("/api/public/v1/images/avatar/987654321"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    public void thatBackgroundImageIsReturned() throws Exception {
        mockMvc.perform(get("/api/public/v1/images/backgrounds/Profile_1.jpg"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    public void thatBackgroundImagesAreReturned() throws Exception {
        mockMvc.perform(get("/api/public/v1/images/backgrounds")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(12)))
            .andExpect(jsonPath("$", hasItems(
                "Profile_1.jpg",
                "Profile_2.jpg",
                "Profile_3.jpg",
                "Profile_4.jpg",
                "Profile_5.jpg",
                "Profile_6.jpg",
                "Profile_7.jpg",
                "Profile_8.jpg",
                "Profile_9.jpg",
                "Profile_10.jpg",
                "Profile_11.jpg",
                "Profile_12.jpg")));
    }

    private String getImageData() {
        return SampleDataFiles.imageToBase64("usersettings/useravatar.jpg");
    }
}
