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

package fi.helsinki.opintoni.web.rest.restrictedapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.ComponentOrderDto;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestrictedPortfolioResourceTest extends RestrictedPortfolioTest {

    private static final String STUDENT_PORTFOLIO_PATH = "/portfolio/student/en/olli-opiskelija";
    private static final String TEACHER_PORTFOLIO_PATH = "/portfolio/teacher/fi/opettaja";

    private static final String PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME = "4c024239-8dab-4ea0-a686-fe373b040f48";

    @Test
    public void thatPortfolioIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    public void thatStudentPortfolioContainsNoLinkedPrivateComponents() throws Exception {
        setPrivateVisibilitiesForEveryComponent();

        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contactInformation").isEmpty())
            .andExpect(jsonPath("$.degrees").isEmpty())
            .andExpect(jsonPath("$.workExperience").isEmpty())
            .andExpect(jsonPath("$.jobSearch").isEmpty())
            .andExpect(jsonPath("$.freeTextContent").isEmpty())
            .andExpect(jsonPath("$.languageProficiencies").isEmpty())
            .andExpect(jsonPath("$.keywords").isEmpty())
            .andExpect(jsonPath("$.summary").isEmpty())
            .andExpect(jsonPath("$.favorites").isEmpty());
    }

    @Test
    public void thatTeacherPortfolioDoesNotContainComponentsLinkedToPrivateSections() throws Exception {
        saveTeacherPortfolioAsRestricted();

        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + TEACHER_PORTFOLIO_PATH)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    both(hasEntry("title", "Globaali tekstikenttä")).and(hasEntry("text", "bla bla bla"))
                )
            )));
    }

    @Test
    public void thatStudentPortfolioContainsComponentOrderInfo() throws Exception {
        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.componentOrders").value(Matchers.<List<ComponentOrderDto>>allOf(
                hasSize(3),
                hasItem(
                    both(hasEntry("component", PortfolioComponent.STUDIES.toString()))
                        .and(hasEntry("orderValue", 1))
                ),
                hasItem(
                    both(hasEntry("component", PortfolioComponent.DEGREES.toString()))
                        .and(hasEntry("orderValue", 2))
                ),
                hasItem(
                    both(hasEntry("component", PortfolioComponent.ATTAINMENTS.toString()))
                        .and(hasEntry("orderValue", 3))
                )
            ))));
    }

    @Test
    public void thatStudentPortfolioOnlyContainsPublicFreeTextContentItems() throws Exception {
        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    hasEntry("instanceName", PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME)
                )
            ))));
    }
}
