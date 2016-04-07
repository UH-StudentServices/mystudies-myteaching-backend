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

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.dto.ProfileDto;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import fi.helsinki.opintoni.service.usefullink.UsefulLinkService;
import fi.helsinki.opintoni.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UsefulLinkService usefulLinkService;
    private final UserSettingsService userSettingsService;
    private final BackgroundImageService backgroundImageService;
    private final UriBuilder uriBuilder;
    private final FavoriteService favoriteService;
    private final AppConfiguration appConfiguration;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserSettingsRepository userSettingsRepository,
                       UsefulLinkService usefulLinkService,
                       UserSettingsService userSettingsService,
                       BackgroundImageService backgroundImageService,
                       UriBuilder uriBuilder,
                       FavoriteService favoriteService,
                       AppConfiguration appConfiguration) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.usefulLinkService = usefulLinkService;
        this.userSettingsService = userSettingsService;
        this.backgroundImageService = backgroundImageService;
        this.uriBuilder = uriBuilder;
        this.favoriteService = favoriteService;
        this.appConfiguration = appConfiguration;
    }

    public Optional<User> findFirstByEduPersonPrincipalName(String eduPersonPrincipalName) {
        return userRepository.findByEduPersonPrincipalName(eduPersonPrincipalName);
    }

    public Optional<User> findFirstByOodiPersonId(String oodiPersonId) {
        return userRepository.findByOodiPersonId(oodiPersonId);
    }

    public User createNewUser(AppUser appUser) {
        User user = new User();
        user.eduPersonPrincipalName = appUser.getEduPersonPrincipalName();
        user.oodiPersonId = appUser.getOodiPersonId();
        user = userRepository.save(user);

        createUserDefaults(user, appUser);
        return user;
    }

    public Optional<ProfileDto> getProfileByOodiPersonId(String oodiPersonId) {
        return findFirstByOodiPersonId(oodiPersonId).map(user -> {
            final UserSettingsDto userSettingsDto = userSettingsService.findByUserId(user.id);
            final String avatarImageUrl = getAvatarImageUrl(user, userSettingsDto);
            final String backgroundImageUrl = userSettingsDto.backgroundUri;
            return new ProfileDto(backgroundImageUrl, avatarImageUrl);
        });
    }

    private void createUserDefaults(User user, AppUser appUser) {
        createUserDefaultSettings(user);
        createUserDefaultUsefulLinks(user, appUser);
        createUserDefaultFavorites(user);
    }

    private void createUserDefaultSettings(User user) {
        UserSettings userSettings = new UserSettings();
        userSettings.user = user;
        userSettings.backgroundFilename = backgroundImageService.getDefaultImageFileName();
        userSettings.showMyStudiesTour = true;
        userSettings.showMyTeachingTour = true;
        userSettings.showPortfolioTour = true;
        userSettings.showBanner = true;
        userSettingsRepository.save(userSettings);
    }

    private void createUserDefaultUsefulLinks(User user, AppUser appUser) {
        usefulLinkService.createUserDefaultUsefulLinks(user, appUser);
    }

    private String getAvatarImageUrl(User user, UserSettingsDto userSettingsDto) {
        return userSettingsDto.hasAvatarImage ?
            uriBuilder.getUserAvatarUrlByOodiPersonId(user.oodiPersonId) :
            uriBuilder.getDefaultUserAvatarUrl();
    }

    private void createUserDefaultFavorites(User user) {
        favoriteService.createDefaultFavorites(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean isAdmin(String eduPersonPrincipalName) {
        List<String> adminAccounts = appConfiguration.getStringValues("adminAccounts");
        return adminAccounts.contains(eduPersonPrincipalName);
    }
}
