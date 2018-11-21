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

import fi.helsinki.opintoni.domain.profile.FreeTextContent;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.dto.profile.ComponentVisibilityDto;
import fi.helsinki.opintoni.dto.profile.FreeTextContentDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.FreeTextContentRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.FreeTextContentConverter;
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
    private final ProfileRepository profileRepository;
    private final ComponentVisibilityService componentVisibilityService;

    @Autowired
    public FreeTextContentService(
        FreeTextContentRepository freeTextContentRepository,
        FreeTextContentConverter freeTextContentConverter,
        ProfileRepository profileRepository,
        ComponentVisibilityService componentVisibilityService) {

        this.freeTextContentRepository = freeTextContentRepository;
        this.freeTextContentConverter = freeTextContentConverter;
        this.profileRepository = profileRepository;
        this.componentVisibilityService = componentVisibilityService;
    }

    public List<FreeTextContentDto> findByProfileId(Long profileId) {
        return freeTextContentRepository
            .findByProfileId(profileId)
            .stream()
            .map(freeTextContentConverter::toDto)
            .collect(toList());
    }

    public List<FreeTextContentDto> findByProfileIdAndComponentVisibilities(Long profileId,
                                                                            List<ComponentVisibilityDto> componentVisibilities) {
        return componentVisibilities.stream()
            .map(v -> findByProfileIdAndComponentVisibility(profileId, v))
            .flatMap(List::stream)
            .collect(toList());
    }

    public FreeTextContentDto insertFreeTextContent(Long profileId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = new FreeTextContent();
        copyDtoProperties(freeTextContent, freeTextContentDto);

        freeTextContent.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        return freeTextContentConverter.toDto(freeTextContentRepository.save(freeTextContent));
    }

    public FreeTextContentDto updateFreeTextContent(Long freeTextContentId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = freeTextContentRepository.findById(freeTextContentId).orElseThrow(NotFoundException::new);
        copyDtoProperties(freeTextContent, freeTextContentDto);

        return freeTextContentDto;
    }

    public void deleteFreeTextContent(Long freeTextContentId, Long profileId, String instanceName) {
        freeTextContentRepository.deleteById(freeTextContentId);

        componentVisibilityService.deleteByProfileIdAndComponentAndInstanceName(profileId,
            ProfileComponent.FREE_TEXT_CONTENT, instanceName);
    }

    private void copyDtoProperties(FreeTextContent freeTextContent, FreeTextContentDto dto) {
        freeTextContent.title = dto.title;
        freeTextContent.text = dto.text;

        if (dto.profileSection != null) {
            freeTextContent.teacherProfileSection = TeacherProfileSection.valueOf(dto.profileSection);
        }

        freeTextContent.instanceName = dto.instanceName != null ? dto.instanceName : UUID.randomUUID().toString();
    }

    private List<FreeTextContentDto> findByProfileIdAndComponentVisibility(Long profileId, ComponentVisibilityDto componentVisibility) {
        TeacherProfileSection teacherProfileSection = componentVisibility.teacherProfileSection != null ?
            TeacherProfileSection.valueOf(componentVisibility.teacherProfileSection) :
            null;

        return freeTextContentRepository
            .findByProfileIdAndTeacherProfileSectionAndInstanceName(profileId, teacherProfileSection, componentVisibility.instanceName)
            .stream()
            .map(freeTextContentConverter::toDto)
            .collect(toList());
    }
}
