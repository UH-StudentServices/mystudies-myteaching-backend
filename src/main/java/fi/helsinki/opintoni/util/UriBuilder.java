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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UriBuilder {

    private final AppConfiguration appConfiguration;

    @Autowired
    public UriBuilder(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public String getSystemBackgroundImageUri(String filename) {
        return getAbsoluteUrl(RestConstants.PUBLIC_API_V1 + "/images/backgrounds/" + filename);
    }

    public String getCustomBackgroundImageUri(String filename) {
        return getAbsoluteUrl(appConfiguration.get("fileStorage.uri") + "/" + filename);
    }

    public String getUserAvatarUrlByPersonId(String personId) {
        return getAbsoluteUrl(RestConstants.PUBLIC_API_V1 + "/images/avatar/" + personId);
    }

    public String getProfileAvatarUrl(ProfileService.ProfileUrlContext profileUrlContext) {
        return getAbsoluteUrl(String.join("/", profileUrlContext.fullPath, "profile-image"));
    }

    public String getCalendarFeedUrl(String feedId) {
        return RestConstants.PUBLIC_API_V1 + "/calendar/" + feedId;
    }

    public String getDefaultUserAvatarUrl() {
        return getAbsoluteUrl("/assets/icons/avatar.png");
    }

    @SuppressWarnings("SameReturnValue")
    public String getProfileDefaultUserAvatarUrl() {
        return "/profile/assets/icons/avatar.png";
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return appConfiguration.get("hostUrl") + relativeUrl;
    }

    public String getProfileUrl(Profile profile) {
        return String.join("/", profileBaseUrl(profile),
            profile.language.getCode(), profile.path);
    }

    public String getProfileUrl(Profile profile, String sharedLinkFragment) {
        return String.join("/", profileBaseUrl(profile), sharedLinkFragment);
    }

    public String getMeceDomain() {
        return appConfiguration.get("mece.domain");
    }

    private String profileBaseUrl(Profile profile) {
        return getProfileBaseUrl(profile.profileRole);
    }

    public String getProfileBaseUrl(ProfileRole role) {
        return appConfiguration.get("profileUrl." + role.getRole());
    }

}
