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

package fi.helsinki.opintoni.security.authorization.profile;

import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Component
public class ProfileRequestResolver {

    private final ProfileService profileService;

    private static final String PATH = "path";
    private static final String PROFILE_ID = "profileId";
    private static final String PROFILE_ROLE = "profileRole";
    private static final String LANG = "lang";
    private static final String SHARED_LINK_FRAGMENT = "sharedLinkFragment";

    @Autowired
    public ProfileRequestResolver(ProfileService profileService) {
        this.profileService = profileService;
    }

    public Optional<ProfileDto> resolve(HttpServletRequest request) {
        Map<String, String> templateVariables = getTemplateVariables(request);

        if (templateVariables.containsKey(PATH)) {
            return getProfileDtoByPath(templateVariables);
        } else if (templateVariables.containsKey(PROFILE_ID)) {
            return getProfileDtoById(templateVariables);
        } else if (templateVariables.containsKey(SHARED_LINK_FRAGMENT)) {
            return getProfileDtoByShareLink(templateVariables);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getTemplateVariables(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    private Optional<ProfileDto> getProfileDtoById(Map<String, String> templateVariables) {
        return Optional.ofNullable(profileService.findById(Long.valueOf(templateVariables.get(PROFILE_ID))));
    }

    private Optional<ProfileDto> getProfileDtoByPath(Map<String, String> templateVariables) {
        return Optional.ofNullable(
            profileService.findByPathAndLangAndRole(
                templateVariables.get(PATH),
                Language.fromCode(templateVariables.get(LANG)),
                ProfileRole.fromValue(templateVariables.get(PROFILE_ROLE)),
                ProfileConverter.ComponentFetchStrategy.NONE
            )
        );
    }

    private Optional<ProfileDto> getProfileDtoByShareLink(Map<String, String> templateVariables) {
        ProfileDto profile = profileService.findBySharedLink(
            templateVariables.get(SHARED_LINK_FRAGMENT),
            ProfileConverter.ComponentFetchStrategy.NONE);

        return Optional.ofNullable(profile);
    }

}
