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
import fi.helsinki.opintoni.dto.portfolio.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.LanguageProficiencyRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.converter.portfolio.LanguageProficiencyConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LanguageProficiencyService {

    private final PermissionChecker permissionChecker;
    private final LanguageProficiencyRepository languageProficiencyRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public LanguageProficiencyService(PermissionChecker permissionChecker,
                                      LanguageProficiencyRepository languageProficiencyRepository,
                                      PortfolioRepository portfolioRepository) {
        this.permissionChecker = permissionChecker;
        this.languageProficiencyRepository = languageProficiencyRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public List<LanguageProficiencyDto> findByPortfolioId(Long id) {
        return languageProficiencyRepository.findByPortfolioId(id).stream()
            .map(LanguageProficiencyConverter::toDto)
            .collect(toList());
    }

    public List<LanguageProficiencyDto> findByPortfolioIdAndVisibility(Long portfolioId, ComponentVisibility.Visibility visibility) {
        return languageProficiencyRepository.findByPortfolioIdAndVisibility(portfolioId, visibility).stream()
            .map(LanguageProficiencyConverter::toDto)
            .collect(toList());
    }

    public void updateLanguageProficiencies(LanguageProficienciesChangeDescriptorDto changeDescriptor,
                                            Long portfolioId,
                                            Long userId) {
        if (changeDescriptor.deletedIds != null) {
            changeDescriptor.deletedIds.forEach(id -> deleteLanguageProficiency(userId, id));
        }

        if (changeDescriptor.updatedLanguageProficiencies != null) {
            changeDescriptor.updatedLanguageProficiencies.forEach((dto) -> updateLanguageProficiency(userId, dto));
        }

        if (changeDescriptor.newLanguageProficiencies != null) {
            changeDescriptor.newLanguageProficiencies.forEach((dto) -> addLanguageProficiency(portfolioId, userId, dto));
        }
    }

    private void addLanguageProficiency(Long portfolioId, Long userId, LanguageProficiencyDto languageProficiencyDto) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        PortfolioLanguageProficiency portfolioLanguageProficiency = new PortfolioLanguageProficiency();
        portfolioLanguageProficiency.languageName = languageProficiencyDto.languageName;
        portfolioLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        portfolioLanguageProficiency.description = languageProficiencyDto.description;
        portfolioLanguageProficiency.portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);
        portfolioLanguageProficiency.visibility = StringUtils.isNotBlank(languageProficiencyDto.visibility) ?
            ComponentVisibility.Visibility.valueOf(languageProficiencyDto.visibility) :
            ComponentVisibility.Visibility.PUBLIC;

        languageProficiencyRepository.save(portfolioLanguageProficiency);
    }

    private void updateLanguageProficiency(Long userId, LanguageProficiencyDto languageProficiencyDto) {
        permissionChecker.verifyPermission(userId, languageProficiencyDto.id, PortfolioLanguageProficiency.class);
        PortfolioLanguageProficiency portfolioLanguageProficiency = languageProficiencyRepository
            .findById(languageProficiencyDto.id).orElseThrow(NotFoundException::new);
        portfolioLanguageProficiency.languageName = languageProficiencyDto.languageName;
        portfolioLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        portfolioLanguageProficiency.description = languageProficiencyDto.description;
        portfolioLanguageProficiency.visibility = StringUtils.isNotBlank(languageProficiencyDto.visibility) ?
            ComponentVisibility.Visibility.valueOf(languageProficiencyDto.visibility) :
            ComponentVisibility.Visibility.PUBLIC;

        languageProficiencyRepository.save(portfolioLanguageProficiency);
    }

    private void deleteLanguageProficiency(Long userId, Long portfolioLanguageProficiencyId) {
        permissionChecker.verifyPermission(userId, portfolioLanguageProficiencyId, PortfolioLanguageProficiency.class);
        languageProficiencyRepository.deleteById(portfolioLanguageProficiencyId);
    }
}
