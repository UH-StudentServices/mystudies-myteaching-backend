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

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.dto.profile.ComponentVisibilityDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.profile.ComponentVisibilityConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.componentvisibility.UpdateComponentVisibilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComponentVisibilityService extends DtoService {

    private final ComponentVisibilityRepository componentVisibilityRepository;
    private final ComponentVisibilityConverter componentVisibilityConverter;
    private final ProfileRepository profileRepository;

    @Autowired
    public ComponentVisibilityService(ComponentVisibilityRepository componentVisibilityRepository,
                                      ComponentVisibilityConverter componentVisibilityConverter,
                                      ProfileRepository profileRepository) {
        this.componentVisibilityRepository = componentVisibilityRepository;
        this.componentVisibilityConverter = componentVisibilityConverter;
        this.profileRepository = profileRepository;
    }

    public List<ComponentVisibilityDto> findByProfileId(Long profileId) {
        return getDtos(profileId,
            componentVisibilityRepository::findByProfileId,
            componentVisibilityConverter::toDto);
    }

    public void update(Long profileId, UpdateComponentVisibilityRequest request) {
        ComponentVisibility componentVisibility = componentVisibilityRepository
            .findByProfileIdAndComponentAndTeacherProfileSectionAndInstanceName(profileId, request.component,
                request.teacherProfileSection, request.instanceName)
            .orElse(new ComponentVisibility());

        componentVisibility.component = request.component;
        componentVisibility.teacherProfileSection = request.teacherProfileSection;
        componentVisibility.instanceName = request.instanceName;
        componentVisibility.visibility = request.visibility;
        componentVisibility.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        componentVisibilityRepository.save(componentVisibility);
    }

    public void save(List<ComponentVisibility> visibilities) {
        componentVisibilityRepository.saveAll(visibilities);
    }

    public void deleteByProfileIdAndComponentAndInstanceName(Long profileId,
                                                             ProfileComponent component,
                                                             String instanceName) {
        componentVisibilityRepository.deleteByProfileIdAndComponentAndInstanceName(profileId, component, instanceName);
    }
}
