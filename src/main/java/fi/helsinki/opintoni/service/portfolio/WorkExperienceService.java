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

import fi.helsinki.opintoni.domain.portfolio.*;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.WorkExperienceRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.WorkExperienceConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.workexperience.UpdateWorkExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

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

    public List<WorkExperienceDto> findByPortfolioIdAndVisibility(Long portfolioId, ComponentVisibility.Visibility visibility) {
        return workExperienceRepository.findByPortfolioIdAndVisibilityOrderByOrderIndexAsc(portfolioId, visibility).stream()
            .map(workExperienceConverter::toDto)
            .collect(toList());
    }

    public List<WorkExperienceDto> updateWorkExperiences(Long portfolioId, List<UpdateWorkExperience> updateWorkExperiences) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);

        workExperienceRepository.deleteByPortfolioId(portfolio.id);

        AtomicInteger orderCounter = new AtomicInteger(0);
        List<WorkExperience> workExperiences = updateWorkExperiences.stream()
            .map(workExperience -> workExperienceConverter.toEntity(workExperience, portfolio, orderCounter.getAndIncrement()))
            .collect(toList());
        workExperienceRepository.saveAll(workExperiences);

        return getDtos(portfolioId,
            workExperienceRepository::findByPortfolioIdOrderByOrderIndexAsc,
            workExperienceConverter::toDto);
    }
}
