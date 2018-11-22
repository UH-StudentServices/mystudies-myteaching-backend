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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.profile.WorkExperienceDto;
import fi.helsinki.opintoni.web.rest.privateapi.profile.workexperience.UpdateWorkExperience;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkExperienceServiceTest extends SpringTest {

    @Autowired
    private WorkExperienceService workExperienceService;

    @Test
    public void thatWorkExperienceIsSaved() {
        final Long profileId = 3L;

        UpdateWorkExperience workExperienceDto = new UpdateWorkExperience();
        workExperienceDto.jobTitle = "Jobtitle";
        workExperienceDto.employer = "Employer name";
        workExperienceDto.employerUrl = "www.employer.invalid";
        workExperienceDto.startDate = LocalDate.now();
        workExperienceDto.endDate = LocalDate.now();
        workExperienceDto.text = "This is text chapter telling what the user has done at the Employer.";

        workExperienceService.updateWorkExperiences(profileId, Collections.singletonList(workExperienceDto));

        List<WorkExperienceDto> workExperienceDtos = workExperienceService.findByProfileId(profileId);

        WorkExperienceDto savedWorkExperienceDto = workExperienceDtos.get(0);

        assertThat(workExperienceDtos).hasSize(1);
        assertThat(savedWorkExperienceDto.jobTitle).isEqualTo(workExperienceDto.jobTitle);
        assertThat(savedWorkExperienceDto.employer).isEqualTo(workExperienceDto.employer);
        assertThat(savedWorkExperienceDto.employerUrl).isEqualTo(workExperienceDto.employerUrl);
        assertThat(savedWorkExperienceDto.startDate).isEqualTo(workExperienceDto.startDate);
        assertThat(savedWorkExperienceDto.endDate).isEqualTo(workExperienceDto.endDate);
        assertThat(savedWorkExperienceDto.text).isEqualTo(workExperienceDto.text);
    }

    @Test
    public void thatWorkExperienceIsReturnedInSameOrderAsSaved() {
        final Long profileId = 3L;

        UpdateWorkExperience workExperienceDto1 = new UpdateWorkExperience();
        workExperienceDto1.jobTitle = "Z First Jobtitle";
        workExperienceDto1.employer = "Z Employer name 1";
        workExperienceDto1.text = "Z This is text chapter telling what the user has done at the Employer.";
        workExperienceDto1.startDate = LocalDate.now();
        workExperienceDto1.endDate = LocalDate.now();
        UpdateWorkExperience workExperienceDto2 = new UpdateWorkExperience();
        workExperienceDto2.jobTitle = "A Second Jobtitle";
        workExperienceDto2.employer = "A Employer name 2";
        workExperienceDto2.text = "A This is text chapter telling what the user has done at the Employer.";
        workExperienceDto2.startDate = LocalDate.now();
        workExperienceDto2.endDate = LocalDate.now();

        workExperienceService.updateWorkExperiences(profileId, Arrays.asList(workExperienceDto1, workExperienceDto2));

        List<WorkExperienceDto> workExperienceDtos = workExperienceService.findByProfileId(profileId);

        WorkExperienceDto savedWorkExperienceDto1 = workExperienceDtos.get(0);
        WorkExperienceDto savedWorkExperienceDto2 = workExperienceDtos.get(1);

        assertThat(workExperienceDtos).hasSize(2);
        assertThat(savedWorkExperienceDto1.jobTitle).isEqualTo(workExperienceDto1.jobTitle);
        assertThat(savedWorkExperienceDto1.employer).isEqualTo(workExperienceDto1.employer);
        assertThat(savedWorkExperienceDto1.text).isEqualTo(workExperienceDto1.text);
        assertThat(savedWorkExperienceDto2.jobTitle).isEqualTo(workExperienceDto2.jobTitle);
        assertThat(savedWorkExperienceDto2.employer).isEqualTo(workExperienceDto2.employer);
        assertThat(savedWorkExperienceDto2.text).isEqualTo(workExperienceDto2.text);
    }

}
