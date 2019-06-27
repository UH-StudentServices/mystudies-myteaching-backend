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
import fi.helsinki.opintoni.domain.profile.Degree;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.DegreeDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.DegreeRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.profile.DegreeConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.degree.UpdateDegree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DegreeService extends DtoService {

    private final DegreeRepository degreeRepository;
    private final DegreeConverter degreeConverter;
    private final ProfileRepository profileRepository;

    @Autowired
    public DegreeService(DegreeRepository degreeRepository,
                         DegreeConverter degreeConverter,
                         ProfileRepository profileRepository) {
        this.degreeRepository = degreeRepository;
        this.degreeConverter = degreeConverter;
        this.profileRepository = profileRepository;
    }

    public List<DegreeDto> findByProfileId(Long profileId) {
        return getDtos(profileId,
            degreeRepository::findByProfileIdOrderByOrderIndexAsc,
            degreeConverter::toDto);
    }

    public List<DegreeDto> findByProfileIdAndVisibility(Long profileId, ComponentVisibility.Visibility visibility) {
        return degreeRepository.findByProfileIdAndVisibilityOrderByOrderIndexAsc(profileId, visibility).stream()
            .map(degreeConverter::toDto)
            .collect(toList());
    }

    public List<DegreeDto> updateDegrees(Long profileId, List<UpdateDegree> updateDegrees) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        degreeRepository.deleteAll(degreeRepository.findByProfileIdOrderByOrderIndexAsc(profile.id));

        AtomicInteger orderCounter = new AtomicInteger(0);
        List<Degree> degrees = updateDegrees.stream()
            .map(updateDegree -> degreeConverter.toEntity(updateDegree, profile, orderCounter.getAndIncrement()))
            .collect(toList());
        degreeRepository.saveAll(degrees);

        return getDtos(profileId,
            degreeRepository::findByProfileIdOrderByOrderIndexAsc,
            degreeConverter::toDto);
    }
}
