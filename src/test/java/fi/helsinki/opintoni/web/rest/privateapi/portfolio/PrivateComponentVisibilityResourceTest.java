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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.domain.portfolio.TeacherPortfolioSection;
import fi.helsinki.opintoni.repository.portfolio.ComponentVisibilityRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.componentvisibility.UpdateComponentVisibilityRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;

import static fi.helsinki.opintoni.domain.portfolio.ComponentVisibility.Visibility;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateComponentVisibilityResourceTest extends SpringTest {

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    private static final String STUDENT_API_PATH = "/portfolio/2/componentvisibility";
    private static final String TEACHER_API_PATH = "/portfolio/4/componentvisibility";
    private static final long STUDENT_PORTFOLIO_ID = 2L;
    private static final long TEACHER_PORTFOLIO_ID = 4L;

    @Test
    public void thatComponentVisibilityIsCreatedForComponent() throws Exception {
        componentVisibilityRepository.deleteAll();

        UpdateComponentVisibilityRequest request = setComponentVisibility(PortfolioComponent.ATTAINMENTS, null, null,
            Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(STUDENT_PORTFOLIO_ID, PortfolioComponent.ATTAINMENTS).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(PortfolioComponent.ATTAINMENTS);
    }

    @Test
    public void thatComponentVisibilityIsCreatedForSectionBoundComponent() throws Exception {
        componentVisibilityRepository.deleteAll();
        final String instanceName = "Testi-ID";

        UpdateComponentVisibilityRequest request = setComponentVisibility(PortfolioComponent.FREE_TEXT_CONTENT,
            TeacherPortfolioSection.RESEARCH, instanceName, Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + TEACHER_API_PATH)
            .with(securityContext(teacherSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponentAndSectionAndInstance(TEACHER_PORTFOLIO_ID,
            PortfolioComponent.FREE_TEXT_CONTENT, TeacherPortfolioSection.RESEARCH, instanceName).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(PortfolioComponent.FREE_TEXT_CONTENT);
        assertThat(permission.teacherPortfolioSection).isEqualTo(TeacherPortfolioSection.RESEARCH);
        assertThat(permission.instanceName).isEqualTo(instanceName);
    }

    @Test
    public void thatComponentVisibilityIsUpdated() throws Exception {
        assertThat(getByIdAndComponent(STUDENT_PORTFOLIO_ID, PortfolioComponent.WORK_EXPERIENCE).isPresent()).isTrue();

        UpdateComponentVisibilityRequest request =
            setComponentVisibility(PortfolioComponent.WORK_EXPERIENCE, null, null, Visibility.PRIVATE);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(STUDENT_PORTFOLIO_ID, PortfolioComponent.WORK_EXPERIENCE).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(PortfolioComponent.WORK_EXPERIENCE);
    }

    private Optional<ComponentVisibility> getByIdAndComponent(Long id, PortfolioComponent component) {
        return componentVisibilityRepository.findByPortfolioIdAndComponent(id, component);
    }

    private Optional<ComponentVisibility> getByIdAndComponentAndSectionAndInstance(Long id,
                                                                                   PortfolioComponent component,
                                                                                   TeacherPortfolioSection section,
                                                                                   String instance) {
        return componentVisibilityRepository
            .findByPortfolioIdAndComponentAndTeacherPortfolioSectionAndInstanceName(id, component, section, instance);
    }

    private UpdateComponentVisibilityRequest setComponentVisibility(PortfolioComponent component,
                                                                    TeacherPortfolioSection section,
                                                                    String instance,
                                                                    Visibility visibility) {
        UpdateComponentVisibilityRequest request = new UpdateComponentVisibilityRequest();
        request.component = component;
        request.visibility = visibility;
        request.teacherPortfolioSection = section;
        request.instanceName = instance;

        return request;
    }

}
