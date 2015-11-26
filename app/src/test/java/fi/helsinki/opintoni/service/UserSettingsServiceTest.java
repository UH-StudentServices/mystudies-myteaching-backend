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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UpdateUserSettingsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.*;

public class UserSettingsServiceTest extends SpringTest {

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Test
    public void thatUserAvatarIsUpdated() throws IOException {
        String imageBase64 = SampleDataFiles.imageToBase64("usersettings/useravatar.jpg");

        userSettingsService.updateUserAvatar(1L, imageBase64);

        UserSettings userSettings = userSettingsRepository.findOne(1L);
        assertTrue(userSettings.userAvatar.imageData.length > 0);
    }

    @Test
    public void thatUserAvatarIsDeleted() throws IOException {
        String imageBase64 = SampleDataFiles.imageToBase64("usersettings/useravatar.jpg");
        userSettingsService.updateUserAvatar(1L, imageBase64);

        UserSettings userSettings = userSettingsRepository.findOne(1L);
        assertTrue(userSettings.userAvatar.imageData.length > 0);

        userSettingsService.deleteUserAvatar(1L);

        userSettings = userSettingsRepository.findOne(1L);
        assertNull(userSettings.userAvatar);
    }

    @Test
    public void thatShowMyStudiesTourIsUpdated() {
        assertTrue(userSettingsRepository.findOne(1L).showMyStudiesTour);

        UpdateUserSettingsRequest request = new UpdateUserSettingsRequest();
        request.showMyStudiesTour = false;

        userSettingsService.update(1L, request);

        assertFalse(userSettingsRepository.findOne(1L).showMyStudiesTour);
    }

    @Test
    public void thatShowMyTeachingTourIsUpdated() {
        assertTrue(userSettingsRepository.findOne(1L).showMyTeachingTour);

        UpdateUserSettingsRequest request = new UpdateUserSettingsRequest();
        request.showMyTeachingTour = false;

        userSettingsService.update(1L, request);

        assertFalse(userSettingsRepository.findOne(1L).showMyTeachingTour);
    }
}
