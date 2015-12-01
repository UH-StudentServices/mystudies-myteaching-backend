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

import fi.helsinki.opintoni.aop.logging.SkipLoggingAspect;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.UserAvatar;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.service.converter.UserSettingsConverter;
import fi.helsinki.opintoni.service.storage.FileStorage;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UpdateUserSettingsRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;


@Service
@Transactional
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserSettingsConverter userSettingsConverter;
    private final FileStorage fileStorage;
    private final BackgroundImageService backgroundImageService;

    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository,
                               UserRepository userRepository,
                               ImageService imageService,
                               UserSettingsConverter userSettingsConverter,
                               FileStorage fileStorage,
                               BackgroundImageService backgroundImageService) {
        this.userSettingsRepository = userSettingsRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.userSettingsConverter = userSettingsConverter;
        this.fileStorage = fileStorage;
        this.backgroundImageService = backgroundImageService;
    }

    public UserSettingsDto findByUserId(Long userId) {
        return userSettingsConverter.toDto(userSettingsRepository.findByUserId(userId));
    }

    public UserSettingsDto update(Long userSettingsId, UpdateUserSettingsRequest request) {
        UserSettings updatedSettings = getUpdatedEntity(userSettingsId, request);
        return userSettingsConverter.toDto(userSettingsRepository.save(updatedSettings));
    }

    @SkipLoggingAspect
    public void updateUserAvatar(Long userSettingsId, String imageBase64) {
        UserSettings userSettings = userSettingsRepository.findOne(userSettingsId);

        if (userSettings.userAvatar == null) {
            userSettings.userAvatar = new UserAvatar();
        }

        userSettings.userAvatar.imageData = imageService.createUserAvatar(imageBase64);

        userSettingsRepository.save(userSettings);
    }

    private UserSettings getUpdatedEntity(Long userSettingsId, UpdateUserSettingsRequest request) {
        UserSettings userSettings = userSettingsRepository.findOne(userSettingsId);
        userSettings.showMyStudiesTour = request.showMyStudiesTour;
        userSettings.showMyTeachingTour = request.showMyTeachingTour;
        userSettings.showPortfolioTour = request.showPortfolioTour;
        return userSettings;
    }

    public BufferedImage getUserAvatarImage(Long userId) {
        UserSettings userSettings = userSettingsRepository.findByUserId(userId);

        if (userSettings.userAvatar == null) {
            return null;
        }

        return imageService.bytesToBufferedImage(userSettings.userAvatar.imageData);
    }

    public BufferedImage getUserBackgroundImage(String oodiPersonId) throws IOException {
        String backgroundFilename = userRepository
            .findByOodiPersonId(oodiPersonId)
            .map(u -> userSettingsRepository.findByUserId(u.id))
            .map(s -> s.backgroundFilename)
            .orElseThrow(notFoundException("Background not found"));

        return backgroundImageService.getBackgroundImage(backgroundFilename);
    }

    public void deleteUserAvatar(Long id) {
        UserSettings userSettings = userSettingsRepository.findOne(id);

        if (userSettings.userAvatar == null) {
            return;
        }

        userSettings.userAvatar.imageData = null;
        userSettingsRepository.save(userSettings);
    }

    public BufferedImage getUserAvatarImageByOodiPersonId(String oodiPersonId) {
        Optional<User> user = userRepository.findByOodiPersonId(oodiPersonId);
        return user.map(u -> u.id)
            .map(this::getUserAvatarImage)
            .orElse(null);
    }

    @SkipLoggingAspect
    public UserSettingsDto updateBackground(Long id, UploadImageBase64Request request) {
        UserSettings userSettings = userSettingsRepository.findOne(id);
        removeOldUploadedBackgroundFile(userSettings);

        byte[] bytes = imageService.createUserBackground(request.imageBase64);
        String filename = getFilename();
        fileStorage.put(filename, bytes);

        userSettings.uploadedBackgroundFilename = filename;
        userSettings.backgroundFilename = null;

        return userSettingsConverter.toDto(userSettingsRepository.save(userSettings));
    }

    private String getFilename() {
        return RandomStringUtils.randomAlphanumeric(64) + ".jpg";
    }

    public UserSettingsDto selectBackground(Long id, SelectBackgroundRequest request) {
        UserSettings userSettings = userSettingsRepository.findOne(id);
        removeOldUploadedBackgroundFile(userSettings);

        userSettings.backgroundFilename = request.filename;
        userSettings.uploadedBackgroundFilename = null;

        return userSettingsConverter.toDto(userSettingsRepository.save(userSettings));
    }

    private void removeOldUploadedBackgroundFile(UserSettings userSettings) {
        if (userSettings.uploadedBackgroundFilename != null) {
            fileStorage.remove(userSettings.uploadedBackgroundFilename);
        }
    }

}
