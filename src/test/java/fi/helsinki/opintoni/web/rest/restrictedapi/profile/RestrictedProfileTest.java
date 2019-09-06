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

package fi.helsinki.opintoni.web.rest.restrictedapi.profile;

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.profile.AbstractProfileResourceTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public abstract class RestrictedProfileTest extends AbstractProfileResourceTest {

    protected static final String RESTRICTED_STUDENT_PROFILE_API_PATH = RestConstants.RESTRICTED_API_V1 + "/profile/2";
    protected static final long STUDENT_PROFILE_ID = 2L;
    protected static final long TEACHER_PROFILE_ID = 4L;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Before
    public final void init() {
        saveStudentProfileAsRestricted();
    }

    private void saveStudentProfileAsRestricted() {
        saveProfileAsRestricted(STUDENT_PROFILE_ID);
    }

    public void saveTeacherProfileAsRestricted() {
        saveProfileAsRestricted(TEACHER_PROFILE_ID);
    }

    public void setPrivateVisibilitiesForEveryComponent() {
        componentVisibilityRepository.deleteAll();
        componentVisibilityRepository.flush();

        Arrays.asList(ProfileComponent.values()).forEach(component -> {
            ComponentVisibility componentVisibility = new ComponentVisibility();
            componentVisibility.component = component;
            componentVisibility.visibility = ComponentVisibility.Visibility.PRIVATE;
            componentVisibility.profile = profileRepository.findById(2L).get();
            componentVisibilityRepository.save(componentVisibility);
        });
    }

    private void saveProfileAsRestricted(long profileId) {
        saveProfileWithVisibility(profileId, ProfileVisibility.RESTRICTED);
    }

    private void saveProfileAsPrivate(long profileId) {
        saveProfileWithVisibility(profileId, ProfileVisibility.PRIVATE);
    }

    protected void saveProfileWithVisibility(long profileId, ProfileVisibility visibility) {
        Profile profile = profileRepository.findById(profileId).get();
        profile.visibility = visibility;
        profileRepository.save(profile);
    }
}
