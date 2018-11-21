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

package fi.helsinki.opintoni.service.converter.profile;

import fi.helsinki.opintoni.domain.profile.*;
import fi.helsinki.opintoni.dto.profile.LanguageProficiencyDto;
import org.apache.commons.lang.StringUtils;

public class LanguageProficiencyConverter {

    public static LanguageProficiencyDto toDto(ProfileLanguageProficiency profileLanguageProficiency) {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.id = profileLanguageProficiency.id;
        languageProficiencyDto.languageName = profileLanguageProficiency.languageName;
        languageProficiencyDto.proficiency = profileLanguageProficiency.proficiency;
        languageProficiencyDto.description = profileLanguageProficiency.description;
        languageProficiencyDto.visibility = profileLanguageProficiency.visibility.toString();
        return languageProficiencyDto;
    }

    public static ProfileLanguageProficiency toEntity(LanguageProficiencyDto languageProficiencyDto, Profile profile) {
        ProfileLanguageProficiency profileLanguageProficiency = new ProfileLanguageProficiency();
        profileLanguageProficiency.languageName = languageProficiencyDto.languageName;
        profileLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        profileLanguageProficiency.description = languageProficiencyDto.description;
        profileLanguageProficiency.profile = profile;
        profileLanguageProficiency.visibility = StringUtils.isNotBlank(languageProficiencyDto.visibility) ?
            ComponentVisibility.Visibility.valueOf(languageProficiencyDto.visibility) :
            ComponentVisibility.Visibility.PUBLIC;
        return profileLanguageProficiency;
    }
}
