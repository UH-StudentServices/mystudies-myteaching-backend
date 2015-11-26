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

import static org.junit.Assert.assertEquals;

public class WorkExperienceServiceTest extends SpringTest {

    @Autowired
    private WorkExperienceService workExperienceService;

    @Test
    public void thatWorkExperienceIsSaved() {
        Long portfolioId = 3L;

        WorkExperienceDto workExperienceDto = new WorkExperienceDto();
        workExperienceDto.jobTitle = "Jobtitle";
        workExperienceDto.employer = "Employer name";
        workExperienceDto.startDate = LocalDate.now();
        workExperienceDto.endDate = LocalDate.now();

        workExperienceService.insert(portfolioId, workExperienceDto);

        List<WorkExperienceDto> workExperienceDtos = workExperienceService.findByPortfolioId(portfolioId);

        WorkExperienceDto savedWorkExperienceDto = workExperienceDtos.get(0);

        assertEquals(1, workExperienceDtos.size());
        assertEquals(workExperienceDto.jobTitle, savedWorkExperienceDto.jobTitle);
        assertEquals(workExperienceDto.employer, savedWorkExperienceDto.employer);
        assertEquals(workExperienceDto.startDate, savedWorkExperienceDto.startDate);
        assertEquals(workExperienceDto.endDate, savedWorkExperienceDto.endDate);
    }
}
