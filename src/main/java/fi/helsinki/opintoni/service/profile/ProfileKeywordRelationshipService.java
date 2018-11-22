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

import fi.helsinki.opintoni.domain.profile.ProfileKeyword;
import fi.helsinki.opintoni.domain.profile.ProfileKeywordRelationship;
import fi.helsinki.opintoni.dto.profile.KeywordDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileKeywordRelationshipRepository;
import fi.helsinki.opintoni.repository.profile.ProfileKeywordRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.ProfileKeywordRelationshipConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.keyword.UpdateKeywordsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileKeywordRelationshipService {

    private final ProfileKeywordRelationshipRepository profileKeywordRelationshipRepository;
    private final ProfileKeywordRelationshipConverter profileKeywordRelationshipConverter;
    private final ProfileRepository profileRepository;
    private final ProfileKeywordRepository profileKeywordRepository;

    @Autowired
    public ProfileKeywordRelationshipService(
        ProfileKeywordRelationshipRepository profileKeywordRelationshipRepository,
        ProfileKeywordRelationshipConverter profileKeywordRelationshipConverter,
        ProfileRepository profileRepository,
        ProfileKeywordRepository profileKeywordRepository) {
        this.profileKeywordRelationshipRepository = profileKeywordRelationshipRepository;
        this.profileKeywordRelationshipConverter = profileKeywordRelationshipConverter;
        this.profileRepository = profileRepository;
        this.profileKeywordRepository = profileKeywordRepository;
    }

    public List<KeywordDto> findByProfileId(Long profileId) {
        return profileKeywordRelationshipRepository.findByProfileIdOrderByOrderIndexAsc(profileId)
            .stream()
            .map(profileKeywordRelationshipConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<KeywordDto> update(Long profileId,
                                   UpdateKeywordsRequest updateKeywordsRequest) {
        List<ProfileKeywordRelationship> profileKeywordRelationships = createProfileKeywordRelationships(
            profileId,
            updateKeywordsRequest);

        profileKeywordRelationshipRepository.deleteByProfileId(profileId);

        return profileKeywordRelationshipRepository.saveAll(profileKeywordRelationships)
            .stream()
            .map(profileKeywordRelationshipConverter::toDto)
            .collect(Collectors.toList());
    }

    private List<ProfileKeywordRelationship> createProfileKeywordRelationships(
        Long profileId,
        UpdateKeywordsRequest updateKeywordsRequest) {
        return updateKeywordsRequest.keywords.stream()
            .distinct()
            .map(keyword -> {
                ProfileKeywordRelationship profileKeywordRelationship = new ProfileKeywordRelationship();
                profileKeywordRelationship.profileKeyword = obtainProfileKeyword(keyword.title);
                profileKeywordRelationship.profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
                profileKeywordRelationship.orderIndex = keyword.orderIndex;
                return profileKeywordRelationship;
            })
            .collect(Collectors.toList());
    }

    private ProfileKeyword obtainProfileKeyword(String title) {
        return profileKeywordRepository.findByTitle(title).orElse(new ProfileKeyword(title));
    }
}
