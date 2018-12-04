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

import fi.helsinki.opintoni.domain.profile.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.dto.profile.StudyAttainmentWhitelistDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StudyAttainmentWhitelistConverter {

    public StudyAttainmentWhitelistDto toDto(StudyAttainmentWhitelist whitelist) {
        StudyAttainmentWhitelistDto whitelistDto = new StudyAttainmentWhitelistDto();
        whitelistDto.showGrades = whitelist.showGrades;
        if (whitelist != null) {
            whitelistDto.oodiStudyAttainmentIds = whitelist.whitelistEntries.stream()
                .map(e -> e.studyAttainmentId)
                .collect(Collectors.toList());
        }
        return whitelistDto;
    }

}
