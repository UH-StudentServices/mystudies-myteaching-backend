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

package fi.helsinki.opintoni.service.profile;

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileBackground;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileBackgroundRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.ImageService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.storage.FileStorage;
import fi.helsinki.opintoni.util.FileNameUtil;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Predicate;

@Service
@Transactional
public class ProfileBackgroundService {

    private static final Predicate<ProfileBackground> HAS_DEFAULT_BACKGROUND = (pb) -> pb.backgroundFilename != null;

    private final ProfileBackgroundRepository profileBackgroundRepository;
    private final ProfileRepository profileRepository;
    private final UserSettingsService userSettingsService;
    private final ImageService imageService;
    private final FileStorage fileStorage;
    private final UriBuilder uriBuilder;

    @Autowired
    public ProfileBackgroundService(ProfileBackgroundRepository profileBackgroundRepository,
                                    ProfileRepository profileRepository,
                                    UserSettingsService userSettingsService,
                                    ImageService imageService,
                                    FileStorage fileStorage,
                                    UriBuilder uriBuilder) {
        this.profileBackgroundRepository = profileBackgroundRepository;
        this.profileRepository = profileRepository;
        this.userSettingsService = userSettingsService;
        this.imageService = imageService;
        this.fileStorage = fileStorage;
        this.uriBuilder = uriBuilder;
    }

    public String getProfileBackgroundUri(Long profileId) {
        return getProfileBackgroundUri(profileRepository.findById(profileId).orElseThrow(NotFoundException::new));
    }

    public String getProfileBackgroundUri(Profile profile) {
        return profileBackgroundRepository.findByProfileId(profile.id)
            .map(backGround -> HAS_DEFAULT_BACKGROUND.test(backGround) ?
                uriBuilder.getSystemBackgroundImageUri(backGround.backgroundFilename) :
                uriBuilder.getCustomBackgroundImageUri(backGround.uploadedBackgroundFilename))
            .orElse(userSettingsService.findByUserId(profile.user.id).backgroundUri);
    }

    public void selectBackground(Long profileId, SelectBackgroundRequest request) {
        ProfileBackground profileBackground = getProfileBackgroundEntity(profileId);
        removeOldBackgroundFile(profileBackground);

        profileBackground.backgroundFilename = request.filename;
        profileBackground.uploadedBackgroundFilename = null;

        profileBackgroundRepository.save(profileBackground);
    }

    public void uploadBackground(Long profileId, UploadImageBase64Request request) {
        ProfileBackground profileBackground = getProfileBackgroundEntity(profileId);
        byte[] bytes = imageService.createUserBackground(request.imageBase64);
        String fileName = FileNameUtil.getImageFileName();

        removeOldBackgroundFile(profileBackground);
        fileStorage.put(fileName, bytes);

        profileBackground.backgroundFilename = null;
        profileBackground.uploadedBackgroundFilename = fileName;

        profileBackgroundRepository.save(profileBackground);
    }

    private ProfileBackground getProfileBackgroundEntity(Long profileId) {
        return profileBackgroundRepository.findByProfileId(profileId)
            .orElseGet(() -> {
                ProfileBackground profileBackground = new ProfileBackground();
                profileBackground.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
                return profileBackground;
            });
    }

    private void removeOldBackgroundFile(ProfileBackground profileBackground) {
        if (profileBackground.uploadedBackgroundFilename != null) {
            fileStorage.remove(profileBackground.uploadedBackgroundFilename);
        }
    }
}
