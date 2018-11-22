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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.profile.componentvisibility.UpdateComponentVisibilityRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;

import static fi.helsinki.opintoni.domain.profile.ComponentVisibility.Visibility;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateComponentVisibilityResourceTest extends SpringTest {

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    private static final String STUDENT_API_PATH = "/profile/2/componentvisibility";
    private static final String TEACHER_API_PATH = "/profile/4/componentvisibility";
    private static final long STUDENT_PROFILE_ID = 2L;
    private static final long TEACHER_PROFILE_ID = 4L;

    @Test
    public void thatComponentVisibilityIsCreatedForComponent() throws Exception {
        componentVisibilityRepository.deleteAll();

        UpdateComponentVisibilityRequest request = setComponentVisibility(ProfileComponent.ATTAINMENTS, null, null,
            Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(STUDENT_PROFILE_ID, ProfileComponent.ATTAINMENTS).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(ProfileComponent.ATTAINMENTS);
    }

    @Test
    public void thatComponentVisibilityIsCreatedForSectionBoundComponent() throws Exception {
        componentVisibilityRepository.deleteAll();
        final String instanceName = "Testi-ID";

        UpdateComponentVisibilityRequest request = setComponentVisibility(ProfileComponent.FREE_TEXT_CONTENT,
            TeacherProfileSection.RESEARCH, instanceName, Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + TEACHER_API_PATH)
            .with(securityContext(teacherSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponentAndSectionAndInstance(TEACHER_PROFILE_ID,
            ProfileComponent.FREE_TEXT_CONTENT, TeacherProfileSection.RESEARCH, instanceName).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(ProfileComponent.FREE_TEXT_CONTENT);
        assertThat(permission.teacherProfileSection).isEqualTo(TeacherProfileSection.RESEARCH);
        assertThat(permission.instanceName).isEqualTo(instanceName);
    }

    @Test
    public void thatComponentVisibilityIsUpdated() throws Exception {
        assertThat(getByIdAndComponent(STUDENT_PROFILE_ID, ProfileComponent.WORK_EXPERIENCE).isPresent()).isTrue();

        UpdateComponentVisibilityRequest request =
            setComponentVisibility(ProfileComponent.WORK_EXPERIENCE, null, null, Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(STUDENT_PROFILE_ID, ProfileComponent.WORK_EXPERIENCE).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(ProfileComponent.WORK_EXPERIENCE);
    }

    private Optional<ComponentVisibility> getByIdAndComponent(Long id, ProfileComponent component) {
        return componentVisibilityRepository.findByProfileIdAndComponent(id, component);
    }

    private Optional<ComponentVisibility> getByIdAndComponentAndSectionAndInstance(Long id,
                                                                                   ProfileComponent component,
                                                                                   TeacherProfileSection section,
                                                                                   String instance) {
        return componentVisibilityRepository
            .findByProfileIdAndComponentAndTeacherProfileSectionAndInstanceName(id, component, section, instance);
    }

    private UpdateComponentVisibilityRequest setComponentVisibility(ProfileComponent component,
                                                                    TeacherProfileSection section,
                                                                    String instance,
                                                                    Visibility visibility) {
        UpdateComponentVisibilityRequest request = new UpdateComponentVisibilityRequest();
        request.component = component;
        request.visibility = visibility;
        request.teacherProfileSection = section;
        request.instanceName = instance;

        return request;
    }

}
