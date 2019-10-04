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

import fi.helsinki.opintoni.domain.profile.ComponentHeading;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.dto.profile.ComponentHeadingDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ComponentHeadingRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.ComponentHeadingConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComponentHeadingService {

    private final ComponentHeadingRepository componentHeadingRepository;
    private final ProfileRepository profileRepository;
    private final ComponentHeadingConverter componentHeadingConverter;

    @Autowired
    public ComponentHeadingService(ComponentHeadingRepository componentHeadingRepository,
                                   ProfileRepository profileRepository,
                                   ComponentHeadingConverter componentHeadingConverter) {
        this.componentHeadingRepository = componentHeadingRepository;
        this.profileRepository = profileRepository;
        this.componentHeadingConverter = componentHeadingConverter;
    }

    public List<ComponentHeadingDto> findByProfileId(Long profileId) {
        return componentHeadingRepository.findByProfileId(profileId).stream()
            .map(componentHeadingConverter::toDto)
            .collect(Collectors.toList());
    }

    public ComponentHeadingDto findByProfileIdAndComponent(Long profileId, ProfileComponent component) {
        return componentHeadingRepository.findByProfileIdAndComponent(profileId, component)
            .map(componentHeadingConverter::toDto)
            .orElse(null);
    }

    public ComponentHeadingDto upsert(Long profileId, ComponentHeadingDto componentHeadingDto) {
        ComponentHeading componentHeading = componentHeadingRepository
            .findByProfileIdAndComponent(profileId, componentHeadingDto.component)
            .orElse(new ComponentHeading());

        componentHeading.component = componentHeadingDto.component;
        componentHeading.heading = componentHeadingDto.heading;
        componentHeading.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        return componentHeadingConverter.toDto(componentHeadingRepository.save(componentHeading));
    }

    public void delete(Long profileId, ProfileComponent component) {
        componentHeadingRepository
            .findByProfileIdAndComponent(profileId, component)
            .ifPresent(componentHeadingRepository::delete);
    }
}
