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

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.WorkExperience;
import fi.helsinki.opintoni.dto.profile.WorkExperienceDto;
import fi.helsinki.opintoni.web.rest.privateapi.profile.workexperience.UpdateWorkExperience;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WorkExperienceConverter {

    public WorkExperienceDto toDto(WorkExperience workExperience) {
        WorkExperienceDto workExperienceDto = new WorkExperienceDto();

        workExperienceDto.employer = workExperience.employer;
        workExperienceDto.employerUrl = workExperience.employerUrl;
        workExperienceDto.jobTitle = workExperience.jobTitle;
        workExperienceDto.startDate = workExperience.startDate;
        workExperienceDto.endDate = workExperience.endDate;
        workExperienceDto.text = workExperience.text;
        workExperienceDto.id = workExperience.id;
        workExperienceDto.visibility = workExperience.visibility.toString();

        return workExperienceDto;
    }

    public WorkExperience toEntity(UpdateWorkExperience updateWorkExperience, Profile profile, int orderIndex) {
        WorkExperience workExperience = new WorkExperience();

        workExperience.employer = updateWorkExperience.employer;
        workExperience.employerUrl = updateWorkExperience.employerUrl;
        workExperience.jobTitle = updateWorkExperience.jobTitle;
        workExperience.startDate = updateWorkExperience.startDate;
        workExperience.endDate = updateWorkExperience.endDate;
        workExperience.text = updateWorkExperience.text;
        workExperience.profile = profile;
        workExperience.orderIndex = orderIndex;
        workExperience.visibility = StringUtils.isNotBlank(updateWorkExperience.visibility) ?
            ComponentVisibility.Visibility.valueOf(updateWorkExperience.visibility) :
            ComponentVisibility.Visibility.PUBLIC;

        return workExperience;
    }
}
