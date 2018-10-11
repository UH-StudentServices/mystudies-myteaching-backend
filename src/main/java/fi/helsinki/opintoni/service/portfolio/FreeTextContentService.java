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

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.domain.portfolio.TeacherPortfolioSection;
import fi.helsinki.opintoni.dto.portfolio.ComponentVisibilityDto;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.FreeTextContentRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.FreeTextContentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class FreeTextContentService {

    private final FreeTextContentRepository freeTextContentRepository;
    private final FreeTextContentConverter freeTextContentConverter;
    private final PortfolioRepository portfolioRepository;
    private final ComponentVisibilityService componentVisibilityService;

    @Autowired
    public FreeTextContentService(
        FreeTextContentRepository freeTextContentRepository,
        FreeTextContentConverter freeTextContentConverter,
        PortfolioRepository portfolioRepository,
        ComponentVisibilityService componentVisibilityService) {

        this.freeTextContentRepository = freeTextContentRepository;
        this.freeTextContentConverter = freeTextContentConverter;
        this.portfolioRepository = portfolioRepository;
        this.componentVisibilityService = componentVisibilityService;
    }

    public List<FreeTextContentDto> findByPortfolioId(Long portfolioId) {
        return freeTextContentRepository
            .findByPortfolioId(portfolioId)
            .stream()
            .map(freeTextContentConverter::toDto)
            .collect(toList());
    }

    public List<FreeTextContentDto> findByPortfolioIdAndComponentVisibilities(Long portfolioId,
                                                                              List<ComponentVisibilityDto> componentVisibilities) {
        return componentVisibilities.stream()
            .map(v -> findByPortfolioIdAndComponentVisibility(portfolioId, v))
            .flatMap(List::stream)
            .collect(toList());
    }

    public FreeTextContentDto insertFreeTextContent(Long portfolioId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = new FreeTextContent();
        copyDtoProperties(freeTextContent, freeTextContentDto);

        freeTextContent.portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);
        return freeTextContentConverter.toDto(freeTextContentRepository.save(freeTextContent));
    }

    public FreeTextContentDto updateFreeTextContent(Long freeTextContentId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = freeTextContentRepository.findById(freeTextContentId).orElseThrow(NotFoundException::new);
        copyDtoProperties(freeTextContent, freeTextContentDto);

        return freeTextContentDto;
    }

    public void deleteFreeTextContent(Long freeTextContentId, Long portfolioId, String instanceName) {
        freeTextContentRepository.deleteById(freeTextContentId);

        componentVisibilityService.deleteByPortfolioIdAndComponentAndInstanceName(portfolioId,
            PortfolioComponent.FREE_TEXT_CONTENT, instanceName);
    }

    private void copyDtoProperties(FreeTextContent freeTextContent, FreeTextContentDto dto) {
        freeTextContent.title = dto.title;
        freeTextContent.text = dto.text;

        if (dto.portfolioSection != null) {
            freeTextContent.teacherPortfolioSection = TeacherPortfolioSection.valueOf(dto.portfolioSection);
        }

        freeTextContent.instanceName = dto.instanceName != null ? dto.instanceName : UUID.randomUUID().toString();
    }

    private List<FreeTextContentDto> findByPortfolioIdAndComponentVisibility(Long portfolioId, ComponentVisibilityDto componentVisibility) {
        TeacherPortfolioSection teacherPortfolioSection = componentVisibility.teacherPortfolioSection != null ?
            TeacherPortfolioSection.valueOf(componentVisibility.teacherPortfolioSection) :
            null;

        return freeTextContentRepository
            .findByPortfolioIdAndTeacherPortfolioSectionAndInstanceName(portfolioId, teacherPortfolioSection, componentVisibility.instanceName)
            .stream()
            .map(freeTextContentConverter::toDto)
            .collect(toList());
    }
}
