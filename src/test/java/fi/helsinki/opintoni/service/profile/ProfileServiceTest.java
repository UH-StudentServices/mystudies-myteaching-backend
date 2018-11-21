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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ProfileServiceTest extends SpringTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ComponentVisibilityService componentVisibilityService;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private static final int TEACHER_PROFILE_SECTION_COUNT = TeacherProfileSection.values().length;
    private static final String PUBLIC_VISIBILITY = "PUBLIC";
    private static final String PRIVATE_VISIBILITY = "PRIVATE";

    @Test
    public void thatProfileIsFoundByPath() {
        ProfileDto profileDto = profileService.findByPathAndLangAndRole("pekka", Language.FI,
            ProfileRole.STUDENT, ProfileConverter.ComponentFetchStrategy.PUBLIC);
        assertThat(profileDto.url).isEqualTo("/profile/fi/pekka");
    }

    @Test
    public void thatProfileIsUpdated() {
        String updatedOwnerName = "Updated owner name";
        String updatedIntro = "Updated intro";
        ProfileVisibility updatedVisibility = ProfileVisibility.PUBLIC;

        ProfileDto profileDto = new ProfileDto();
        profileDto.ownerName = updatedOwnerName;
        profileDto.intro = updatedIntro;
        profileDto.visibility = updatedVisibility;

        ProfileDto updateResult = profileService.update(1L, profileDto);

        assertThat(updateResult.ownerName).isEqualTo(updatedOwnerName);
        assertThat(updateResult.intro).isEqualTo(updatedIntro);
        assertThat(updateResult.visibility).isEqualTo(updatedVisibility);
    }

    @Test
    public void thatTeacherProfileAndComponentVisibilitiesAreCreated() {
        componentVisibilityRepository.deleteAll();
        deleteExistingTeacherProfile();

        assertThat(profileRepository.findByUserId(4L).count()).isZero();

        profileService.insert(4L, "Olli Opettaja", ProfileRole.TEACHER, Language.FI);

        Profile profile = profileRepository.findByUserId(4L).findFirst().get();
        assertThat(profile.visibility).isEqualTo(ProfileVisibility.PRIVATE);
        assertThat(profile.profileRole).isEqualTo(ProfileRole.TEACHER);
        assertThat(componentVisibilityService.findByProfileId(profile.id))
            .hasSize(TEACHER_PROFILE_SECTION_COUNT)
            .extracting("teacherProfileSection", "visibility")
            .contains(
                tuple("BASIC_INFORMATION", PUBLIC_VISIBILITY),
                tuple("RESEARCH", PRIVATE_VISIBILITY),
                tuple("TEACHING", PRIVATE_VISIBILITY),
                tuple("ADMINISTRATION", PRIVATE_VISIBILITY));
    }

    private void deleteExistingTeacherProfile() {
        profileRepository.deleteById(4L);
    }
}
