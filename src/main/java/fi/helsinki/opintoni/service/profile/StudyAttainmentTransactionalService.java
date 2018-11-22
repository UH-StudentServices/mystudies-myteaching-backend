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
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.ProfileStudyAttainmentWhitelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudyAttainmentTransactionalService {

    private final ProfileStudyAttainmentWhitelistRepository whitelistRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public StudyAttainmentTransactionalService(ProfileStudyAttainmentWhitelistRepository whitelistRepository,
                                               ProfileRepository profileRepository) {
        this.whitelistRepository = whitelistRepository;
        this.profileRepository = profileRepository;
    }

    public Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    public Optional<StudyAttainmentWhitelist> findByProfileId(Long profileId) {
        return whitelistRepository.findByProfileId(profileId);
    }
}
