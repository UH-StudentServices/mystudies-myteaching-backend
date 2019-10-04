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

import fi.helsinki.opintoni.domain.profile.JobSearch;
import fi.helsinki.opintoni.dto.profile.JobSearchDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.JobSearchRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.JobSearchConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class JobSearchService {

    private final JobSearchRepository jobSearchRepository;
    private final ProfileRepository profileRepository;
    private final JobSearchConverter jobSearchConverter;

    @Autowired
    public JobSearchService(JobSearchRepository jobSearchRepository,
                            JobSearchConverter jobSearchConverter,
                            ProfileRepository profileRepository) {
        this.jobSearchRepository = jobSearchRepository;
        this.profileRepository = profileRepository;
        this.jobSearchConverter = jobSearchConverter;
    }

    public JobSearchDto findByProfileId(Long profileId) {
        return jobSearchRepository.findByProfileId(profileId)
            .map(jobSearchConverter::toDto)
            .orElse(null);
    }

    public JobSearchDto insert(Long profileId, JobSearchDto jobSearchDto) {
        Optional<JobSearch> jobSearchOptional = jobSearchRepository.findByProfileId(profileId);
        JobSearch jobSearch;

        if (jobSearchOptional.isPresent()) {
            jobSearch = jobSearchOptional.get();
        } else {
            jobSearch = new JobSearch();
            jobSearch.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        }
        jobSearch.contactEmail = jobSearchDto.contactEmail;
        jobSearch.headline = jobSearchDto.headline;
        jobSearch.text = jobSearchDto.text;

        return jobSearchConverter.toDto(jobSearchRepository.save(jobSearch));
    }

    public void delete(Long profileId) {
        jobSearchRepository
            .findByProfileId(profileId)
            .ifPresent(jobSearchRepository::delete);
    }
}
