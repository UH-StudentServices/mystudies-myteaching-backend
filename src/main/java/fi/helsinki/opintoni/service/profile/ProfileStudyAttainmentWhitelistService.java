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

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.domain.profile.StudyAttainmentWhitelistEntry;
import fi.helsinki.opintoni.dto.profile.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.ProfileStudyAttainmentWhitelistEntryRepository;
import fi.helsinki.opintoni.repository.profile.ProfileStudyAttainmentWhitelistRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.profile.StudyAttainmentWhitelistConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileStudyAttainmentWhitelistService extends DtoService {

    private final ProfileStudyAttainmentWhitelistEntryRepository entryRepository;

    private final ProfileStudyAttainmentWhitelistRepository whitelistRepository;

    private final StudyAttainmentWhitelistConverter whitelistConverter;

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileStudyAttainmentWhitelistService(ProfileStudyAttainmentWhitelistEntryRepository entryRepository,
                                                  ProfileStudyAttainmentWhitelistRepository whitelistRepository,
                                                  StudyAttainmentWhitelistConverter whitelistConverter,
                                                  ProfileRepository profileRepository) {
        this.entryRepository = entryRepository;
        this.whitelistRepository = whitelistRepository;
        this.whitelistConverter = whitelistConverter;
        this.profileRepository = profileRepository;
    }

    public StudyAttainmentWhitelistDto get(Long profileId) {
        return getDto(profileId,
            whitelistRepository::findByProfileId,
            whitelistConverter::toDto);
    }

    public void insert(Profile profile) {
        StudyAttainmentWhitelist whitelist = new StudyAttainmentWhitelist();
        whitelist.profile = profile;
        whitelist.whitelistEntries = new ArrayList<>();
        whitelist.showGrades = true;
        whitelistRepository.save(whitelist);
    }

    public StudyAttainmentWhitelistDto update(Long profileId, StudyAttainmentWhitelistDto whitelistDto) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        whitelistRepository.deleteByProfileId(profileId);
        StudyAttainmentWhitelist whitelist = new StudyAttainmentWhitelist();
        whitelist.showGrades = whitelistDto.showGrades;
        whitelist.profile = profile;
        whitelistRepository.save(whitelist);
        whitelist.whitelistEntries = whitelistDto.oodiStudyAttainmentIds
            .stream().map(i -> createWhitelistEntry(whitelist, i))
            .collect(Collectors.toList());
        return whitelistConverter.toDto(whitelist);
    }

    private StudyAttainmentWhitelistEntry createWhitelistEntry(StudyAttainmentWhitelist whitelist,
                                                               Long studyAttainmentId) {
        StudyAttainmentWhitelistEntry entry = new StudyAttainmentWhitelistEntry();
        entry.whitelist = whitelist;
        entry.studyAttainmentId = studyAttainmentId;
        return entryRepository.save(entry);
    }

}
