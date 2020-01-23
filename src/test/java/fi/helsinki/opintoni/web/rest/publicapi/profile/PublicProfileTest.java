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

package fi.helsinki.opintoni.web.rest.publicapi.profile;

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Degree;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.ProfileLanguageProficiency;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.domain.profile.Sample;
import fi.helsinki.opintoni.domain.profile.WorkExperience;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.profile.DegreeRepository;
import fi.helsinki.opintoni.repository.profile.LanguageProficiencyRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.SampleRepository;
import fi.helsinki.opintoni.repository.profile.WorkExperienceRepository;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.profile.AbstractProfileResourceTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class PublicProfileTest extends AbstractProfileResourceTest {

    protected static final String PUBLIC_STUDENT_PROFILE_API_PATH = RestConstants.PUBLIC_API_V1 + "/profile/2";

    protected static final String ACTIVE_SHARED_LINK = "/a3728b39-7099-4f8c-9413-da2817eeccf9";
    protected static final String EXPIRED_SHARED_LINK = "/b2672af7-306f-43aa-ab3f-acbc6a41f47f";

    protected static final String ACTIVE_SHARED_STUDENT_PROFILE_API_PATH = RestConstants.PUBLIC_API_V1 + "/profile" + ACTIVE_SHARED_LINK;
    protected static final String EXPIRED_SHARED_STUDENT_PROFILE_API_PATH = RestConstants.PUBLIC_API_V1 + "/profile" + EXPIRED_SHARED_LINK;

    protected static final long STUDENT_PROFILE_ID = 2L;
    protected static final long TEACHER_PROFILE_ID = 4L;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LanguageProficiencyRepository languageProficiencyRepository;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Autowired
    private DegreeRepository degreeRepository;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Before
    public void saveStudentProfileAsPublic() {
        saveProfileAsPublic(STUDENT_PROFILE_ID);
    }

    public void saveTeacherProfileAsPublic() {
        saveProfileAsPublic(TEACHER_PROFILE_ID);
    }

    public void setPrivateVisibilityForEveryStudentProfileComponent() {
        componentVisibilityRepository.deleteAll();
        componentVisibilityRepository.flush();

        Arrays.asList(ProfileComponent.values()).forEach(component -> {
            ComponentVisibility componentVisibility = new ComponentVisibility();
            componentVisibility.component = component;
            componentVisibility.visibility = ComponentVisibility.Visibility.PRIVATE;
            componentVisibility.profile = profileRepository.findById(STUDENT_PROFILE_ID).get();
            componentVisibilityRepository.save(componentVisibility);
        });
    }

    public void setPrivateVisibilityForEveryStudentProfileComponentItem() {
        List<ProfileLanguageProficiency> proficiencies = languageProficiencyRepository.findByProfileIdOrderByOrderIndexAsc(STUDENT_PROFILE_ID)
            .stream()
            .peek(proficiency -> proficiency.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        languageProficiencyRepository.saveAll(proficiencies);

        List<Sample> samples = sampleRepository.findByProfileIdOrderByOrderIndexAsc(STUDENT_PROFILE_ID).stream()
            .peek(sample -> sample.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        sampleRepository.saveAll(samples);

        List<Degree> degrees = degreeRepository.findByProfileIdOrderByOrderIndexAsc(STUDENT_PROFILE_ID).stream()
            .peek(degree -> degree.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        degreeRepository.saveAll(degrees);

        List<WorkExperience> workExperiences = workExperienceRepository.findByProfileIdOrderByOrderIndexAsc(STUDENT_PROFILE_ID).stream()
            .peek(workExperience -> workExperience.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        workExperienceRepository.saveAll(workExperiences);
    }

    private void saveProfileAsPublic(long profileId) {
        Profile profile = profileRepository.findById(profileId).get();
        profile.visibility = ProfileVisibility.PUBLIC;
        profileRepository.save(profile);
    }
}
