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

import fi.helsinki.opintoni.domain.profile.*;
import fi.helsinki.opintoni.dto.profile.SampleDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.SampleRepository;
import fi.helsinki.opintoni.service.converter.profile.SampleConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.sample.UpdateSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SampleService {

    private final SampleRepository sampleRepository;
    private final ProfileRepository profileRepository;
    private final SampleConverter sampleConverter;

    @Autowired
    public SampleService(SampleRepository sampleRepository,
                         ProfileRepository profileRepository,
                         SampleConverter sampleConverter) {
        this.sampleRepository = sampleRepository;
        this.profileRepository = profileRepository;
        this.sampleConverter = sampleConverter;
    }

    public List<SampleDto> findByProfileId(Long profileId) {
        List<Sample> samples = sampleRepository.findByProfileIdOrderByOrderIndexAsc(profileId);
        return samples.stream().map(sampleConverter::toDto).collect(toList());
    }

    public List<SampleDto> findByProfileIdAndVisibility(Long profileId, ComponentVisibility.Visibility visibility) {
        return sampleRepository.findByProfileIdAndVisibilityOrderByOrderIndexAsc(profileId, visibility).stream()
            .map(sampleConverter::toDto)
            .collect(toList());
    }

    public List<SampleDto> updateSamples(Long profileId, List<UpdateSample> updateSamples) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        sampleRepository.deleteByProfileId(profile.id);

        AtomicInteger orderCounter = new AtomicInteger(0);
        List<Sample> samples = updateSamples.stream()
            .map(updateSample -> sampleConverter.toEntity(updateSample, profile, orderCounter.getAndIncrement()))
            .collect(toList());
        sampleRepository.saveAll(samples);

        return findByProfileId(profileId);
    }
}
