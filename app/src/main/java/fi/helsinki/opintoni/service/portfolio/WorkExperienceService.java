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

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.WorkExperience;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.WorkExperienceRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.WorkExperienceConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.workexperience.UpdateWorkExperience;
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
        return getDtos(portfolioId, workExperienceRepository::findByPortfolioIdOrderByOrderIndexAsc, workExperienceConverter::toDto);
    }

    public List<WorkExperienceDto> updateWorkExperiences(Long portfolioId, List<UpdateWorkExperience> updateWorkExperiences) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        workExperienceRepository.deleteByPortfolioId(portfolio.id);
        int orderIndex = 0;

        for (UpdateWorkExperience updateWorkExperience: updateWorkExperiences) {
            WorkExperience workExperience = new WorkExperience();
            workExperience.employer = updateWorkExperience.employer;
            workExperience.employerUrl = updateWorkExperience.employerUrl;
            workExperience.jobTitle = updateWorkExperience.jobTitle;
            workExperience.startDate = updateWorkExperience.startDate;
            workExperience.endDate = updateWorkExperience.endDate;
            workExperience.text = updateWorkExperience.text;
            workExperience.portfolio = portfolio;
            workExperience.orderIndex = orderIndex++;
            workExperienceRepository.save(workExperience);
        }

        return getDtos(portfolioId,
            workExperienceRepository::findByPortfolioIdOrderByOrderIndexAsc,
            workExperienceConverter::toDto);
    }

    public void orderWorkExperiences(final Long portfolioId, final List<Long> orderedWorkExperienceIds) {
        workExperienceRepository
            .findByPortfolioIdOrderByOrderIndexAsc(portfolioId).stream()
            .filter(w -> orderedWorkExperienceIds.contains(w.id))
            .forEach(w -> w.orderIndex = orderedWorkExperienceIds.indexOf(w.id));
    }
}
