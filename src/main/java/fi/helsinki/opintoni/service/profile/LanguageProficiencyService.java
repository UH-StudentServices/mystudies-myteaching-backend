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
import fi.helsinki.opintoni.dto.profile.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.profile.LanguageProficiencyDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.LanguageProficiencyRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.converter.profile.LanguageProficiencyConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LanguageProficiencyService {

    private final PermissionChecker permissionChecker;
    private final LanguageProficiencyRepository languageProficiencyRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public LanguageProficiencyService(PermissionChecker permissionChecker,
                                      LanguageProficiencyRepository languageProficiencyRepository,
                                      ProfileRepository profileRepository) {
        this.permissionChecker = permissionChecker;
        this.languageProficiencyRepository = languageProficiencyRepository;
        this.profileRepository = profileRepository;
    }

    public List<LanguageProficiencyDto> findByProfileId(Long id) {
        return languageProficiencyRepository.findByProfileId(id).stream()
            .map(LanguageProficiencyConverter::toDto)
            .collect(toList());
    }

    public List<LanguageProficiencyDto> findByProfileIdAndVisibility(Long profileId, ComponentVisibility.Visibility visibility) {
        return languageProficiencyRepository.findByProfileIdAndVisibility(profileId, visibility).stream()
            .map(LanguageProficiencyConverter::toDto)
            .collect(toList());
    }

    public void updateLanguageProficiencies(LanguageProficienciesChangeDescriptorDto changeDescriptor,
                                            Long profileId,
                                            Long userId) {
        if (changeDescriptor.deletedIds != null) {
            changeDescriptor.deletedIds.forEach(id -> deleteLanguageProficiency(userId, id));
        }

        if (changeDescriptor.updatedLanguageProficiencies != null) {
            changeDescriptor.updatedLanguageProficiencies.forEach((dto) -> updateLanguageProficiency(userId, dto));
        }

        if (changeDescriptor.newLanguageProficiencies != null) {
            changeDescriptor.newLanguageProficiencies.forEach((dto) -> addLanguageProficiency(profileId, userId, dto));
        }
    }

    private void addLanguageProficiency(Long profileId, Long userId, LanguageProficiencyDto languageProficiencyDto) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);

        languageProficiencyRepository.save(LanguageProficiencyConverter.toEntity(
            languageProficiencyDto,
            profileRepository.findById(profileId).orElseThrow(NotFoundException::new)
        ));
    }

    private void updateLanguageProficiency(Long userId, LanguageProficiencyDto languageProficiencyDto) {
        permissionChecker.verifyPermission(userId, languageProficiencyDto.id, ProfileLanguageProficiency.class);
        ProfileLanguageProficiency profileLanguageProficiency = languageProficiencyRepository
            .findById(languageProficiencyDto.id).orElseThrow(NotFoundException::new);
        profileLanguageProficiency.languageName = languageProficiencyDto.languageName;
        profileLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        profileLanguageProficiency.description = languageProficiencyDto.description;
        profileLanguageProficiency.visibility = StringUtils.isNotBlank(languageProficiencyDto.visibility) ?
            ComponentVisibility.Visibility.valueOf(languageProficiencyDto.visibility) :
            ComponentVisibility.Visibility.PUBLIC;

        languageProficiencyRepository.save(profileLanguageProficiency);
    }

    private void deleteLanguageProficiency(Long userId, Long profileLanguageProficiencyId) {
        permissionChecker.verifyPermission(userId, profileLanguageProficiencyId, ProfileLanguageProficiency.class);
        languageProficiencyRepository.deleteById(profileLanguageProficiencyId);
    }
}
