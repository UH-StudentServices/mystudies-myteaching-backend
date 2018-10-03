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

import fi.helsinki.opintoni.domain.portfolio.SkillsAndExpertise;
import fi.helsinki.opintoni.dto.portfolio.SkillsAndExpertiseDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.SkillsAndExpertiseRepository;
import fi.helsinki.opintoni.service.converter.portfolio.SkillsAndExpertiseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SkillsAndExpertiseService {

    private final PortfolioRepository portfolioRepository;
    private final SkillsAndExpertiseRepository skillsAndExpertiseRepository;
    private final SkillsAndExpertiseConverter skillsAndExpertiseConverter;

    @Autowired
    public SkillsAndExpertiseService(PortfolioRepository portfolioRepository,
                                     SkillsAndExpertiseRepository skillsAndExpertiseRepository,
                                     SkillsAndExpertiseConverter skillsAndExpertiseConverter) {
        this.portfolioRepository = portfolioRepository;
        this.skillsAndExpertiseRepository = skillsAndExpertiseRepository;
        this.skillsAndExpertiseConverter = skillsAndExpertiseConverter;
    }

    public SkillsAndExpertiseDto updateSkillsAndExpertise(Long portfolioId, SkillsAndExpertiseDto skillsAndExpertiseDto) {
        SkillsAndExpertise skillsAndExpertise =
            skillsAndExpertiseRepository.findByPortfolioId(portfolioId).orElse(new SkillsAndExpertise());

        if (skillsAndExpertise.portfolio == null) {
            skillsAndExpertise.portfolio = portfolioRepository.findOne(portfolioId);
        }

        copyDtoProperties(skillsAndExpertise, skillsAndExpertiseDto);
        return skillsAndExpertiseConverter.toDto(skillsAndExpertiseRepository.save(skillsAndExpertise));
    }

    public SkillsAndExpertiseDto findByPortfolioId(Long portfolioId) {
        Optional<SkillsAndExpertise> skillsAndExpertise = skillsAndExpertiseRepository.findByPortfolioId(portfolioId);
        return skillsAndExpertise.map(skillsAndExpertiseConverter::toDto).orElse(null);
    }

    private void copyDtoProperties(SkillsAndExpertise skillsAndExpertise, SkillsAndExpertiseDto skillsAndExpertiseDto) {
        skillsAndExpertise.skillsAndExpertise = skillsAndExpertiseDto.skillsAndExpertise;
    }
}
