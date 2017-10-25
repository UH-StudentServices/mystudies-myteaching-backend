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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.integration.mece.JWTService;
import fi.helsinki.opintoni.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class UserSettingsConverter {

    private final static Predicate<UserSettings> HAS_DEFAULT_BACKGROUND = (us) -> us.backgroundFilename != null;

    private final UriBuilder uriBuilder;

    private final JWTService jwtService;

    @Autowired
    public UserSettingsConverter(UriBuilder uriBuilder, JWTService jwtService) {
        this.uriBuilder = uriBuilder;
        this.jwtService = jwtService;
    }

    private enum BackgroundType {
        DEFAULT, CUSTOM
    }

    public UserSettingsDto toDto(UserSettings userSettings) {
        UserSettingsDto userSettingsDto = new UserSettingsDto();
        userSettingsDto.id = userSettings.id;
        userSettingsDto.backgroundUri = getBackgroundUri(userSettings);
        userSettingsDto.backgroundType = getBackgroundType(userSettings);
        userSettingsDto.hasAvatarImage = userSettings.hasAvatarImage();
        userSettingsDto.showBanner = userSettings.showBanner;
        userSettingsDto.cookieConsent = userSettings.cookieConsent;
        userSettingsDto.meceJWTToken = jwtService.generateToken(userSettings.user.eduPersonPrincipalName);
        userSettingsDto.meceDomain = uriBuilder.getMeceDomain();
        return userSettingsDto;
    }

    private String getBackgroundType(UserSettings userSettings) {
        return HAS_DEFAULT_BACKGROUND.test(userSettings) ? BackgroundType.DEFAULT.name() : BackgroundType.CUSTOM.name();
    }

    private String getBackgroundUri(UserSettings userSettings) {
        return HAS_DEFAULT_BACKGROUND.test(userSettings)
            ? uriBuilder.getSystemBackgroundImageUri(userSettings.backgroundFilename)
            : uriBuilder.getCustomBackgroundImageUri(userSettings.uploadedBackgroundFilename);
    }

}
