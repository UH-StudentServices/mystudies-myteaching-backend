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

import fi.helsinki.opintoni.domain.portfolio.WorkExperience;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.WorkExperienceRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.WorkExperienceConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class WorkExperienceService extends DtoService {
    private final WorkExperienceRepository workExperienceRepository;
    private final PortfolioRepository portfolioRepository;
    private final WorkExperienceConverter workExperienceConverter;

    @Autowired
    public WorkExperienceService(WorkExperienceRepository workExperienceRepository,
                                 PortfolioRepository portfolioRepository,
                                 WorkExperienceConverter workExperienceConverter) {
        this.workExperienceRepository = workExperienceRepository;
        this.portfolioRepository = portfolioRepository;
        this.workExperienceConverter = workExperienceConverter;
    }

    public List<WorkExperienceDto> findByPortfolioId(Long portfolioId) {
        return getDtos(portfolioId, workExperienceRepository::findByPortfolioId, workExperienceConverter::toDto);
    }

    public WorkExperienceDto insert(Long portfolioId, WorkExperienceDto workExperienceDto) {
        WorkExperience workExperience = new WorkExperience();
        workExperience.employer = workExperienceDto.employer;
        workExperience.employerUrl = workExperienceDto.employerUrl;
        workExperience.startDate = workExperienceDto.startDate;
        workExperience.endDate = workExperienceDto.endDate;
        workExperience.jobTitle = workExperienceDto.jobTitle;
        workExperience.text = workExperienceDto.text;
        workExperience.portfolio = portfolioRepository.findOne(portfolioId);
        return workExperienceConverter.toDto(workExperienceRepository.save(workExperience));
    }

    public List<WorkExperienceDto> delete(Long workExperienceId, Long portfolioId) {
        workExperienceRepository.delete(workExperienceId);
        return findByPortfolioId(portfolioId);
    }
}
