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

import fi.helsinki.opintoni.domain.profile.ComponentOrder;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.ComponentOrderDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ComponentOrderRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.ComponentOrderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ComponentOrderService {

    private final ComponentOrderRepository componentOrderRepository;
    private final ComponentOrderConverter componentOrderConverter;
    private final ProfileRepository profileRepository;

    @Autowired
    public ComponentOrderService(ComponentOrderRepository componentOrderRepository,
                                 ComponentOrderConverter componentOrderConverter,
                                 ProfileRepository profileRepository) {
        this.componentOrderRepository = componentOrderRepository;
        this.componentOrderConverter = componentOrderConverter;
        this.profileRepository = profileRepository;
    }

    public List<ComponentOrderDto> findByProfileId(Long profileId) {
        return componentOrderRepository.findByProfileId(profileId).stream()
            .map(componentOrderConverter::toDto)
            .collect(toList());
    }

    public List<ComponentOrderDto> update(Long profileId, List<ComponentOrderDto> componentOrders) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("Profile not found"));

        componentOrderRepository.deleteByProfileId(profileId);
        componentOrderRepository.flush();

        return componentOrders.stream()
            .map(dto -> {
                ComponentOrder co = componentOrderRepository.save(componentOrderConverter.toEntity(profile, dto));

                return componentOrderConverter.toDto(co);
            })
            .collect(toList());
    }
}
