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

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkExperienceServiceTest extends SpringTest {

    @Autowired
    private WorkExperienceService workExperienceService;

    @Test
    public void thatWorkExperienceIsSaved() {
        Long portfolioId = 3L;

        WorkExperienceDto workExperienceDto = new WorkExperienceDto();
        workExperienceDto.jobTitle = "Jobtitle";
        workExperienceDto.employer = "Employer name";
        workExperienceDto.employerUrl = "www.employer.invalid";
        workExperienceDto.startDate = LocalDate.now();
        workExperienceDto.endDate = LocalDate.now();
        workExperienceDto.text = "This is text chapter telling what the user has done at the Employer.";

        workExperienceService.insert(portfolioId, workExperienceDto);

        List<WorkExperienceDto> workExperienceDtos = workExperienceService.findByPortfolioId(portfolioId);

        WorkExperienceDto savedWorkExperienceDto = workExperienceDtos.get(0);

        assertThat(workExperienceDtos).hasSize(1);
        assertThat(savedWorkExperienceDto.jobTitle).isEqualTo(workExperienceDto.jobTitle);
        assertThat(savedWorkExperienceDto.employer).isEqualTo(workExperienceDto.employer);
        assertThat(savedWorkExperienceDto.employerUrl).isEqualTo(workExperienceDto.employerUrl);
        assertThat(savedWorkExperienceDto.startDate).isEqualTo(workExperienceDto.startDate);
        assertThat(savedWorkExperienceDto.endDate).isEqualTo(workExperienceDto.endDate);
        assertThat(savedWorkExperienceDto.text).isEqualTo(workExperienceDto.text);
    }
}
