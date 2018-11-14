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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.*;
import fi.helsinki.opintoni.service.portfolio.ComponentHeadingService;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicPortfolioResourceTest extends PublicPortfolioTest {

    private static final String STUDENT_PORTFOLIO_PATH = "/profile/student/en/olli-opiskelija";
    private static final String TEACHER_PORTFOLIO_PATH = "/profile/teacher/fi/opettaja";
    private static final long STUDENT_PORTFOLIO_ID = 2L;

    private static final String PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME = "4c024239-8dab-4ea0-a686-fe373b040f48";

    private static final String SHARED_LINK_PATH = "/profile/shared";
    private static final String ACTIVE_SHARED_LINK = "a3728b39-7099-4f8c-9413-da2817eeccf9";
    private static final String EXPIRED_SHARED_LINK = "b2672af7-306f-43aa-ab3f-acbc6a41f47f";

    @Autowired
    private ComponentHeadingService componentHeadingService;

    @Test
    public void thatPortfolioIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    public void thatStudentPortfolioContainsNoLinkedPrivateComponents() throws Exception {
        setPrivateVisibilityForEveryStudentPortfolioComponent();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH)
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
            .andExpect(jsonPath("$.samples").isEmpty());
    }

    @Test
    public void thatStudentPortfolioContainsNoLinkedPrivateComponentItems() throws Exception {
        setPrivateVisibilityForPortfolioComponentItems(STUDENT_PORTFOLIO_ID);
        saveStudentPortfolioAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.degrees").isEmpty())
            .andExpect(jsonPath("$.workExperience").isEmpty())
            .andExpect(jsonPath("$.languageProficiencies").isEmpty())
            .andExpect(jsonPath("$.samples").isEmpty());
    }

    @Test
    public void thatTeacherPortfolioDoesNotContainComponentsLinkedToPrivateSections() throws Exception {
        saveTeacherPortfolioAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PORTFOLIO_PATH)
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
    public void thatPrivatePortfolioIsNotFoundFromPublicApi() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatPublicTeacherPortfolioContainsPublicContactInformation() throws Exception {
        saveTeacherPortfolioAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contactInformation.email")
                .value("olli.opettaja@helsinki.fi"));
    }

    @Test
    public void thatStudentPortfolioContainsComponentOrderInfo() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH)
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
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    hasEntry("instanceName", PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME)
                )
            ))));
    }

    @Test
    public void thatAllHeadingsForPublicComponentsAreReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.headings").value(Matchers.<List<ComponentHeadingDto>>allOf(
                hasSize(2),
                hasItem(
                    both(hasEntry("component", PortfolioComponent.STUDIES.toString()))
                        .and(hasEntry("heading", "Test heading"))
                ),
                hasItem(
                    both(hasEntry("component", PortfolioComponent.DEGREES.toString()))
                        .and(hasEntry("heading", "Another heading"))
                )
            ))));
    }

    @Test
    public void thatStudentPortfolioWorkExperiencesAreOrderedProperly() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.workExperience").value(
                contains(hasEntry("jobTitle", "Harjoittelija"),
                    hasEntry("jobTitle", "Rehtori"),
                    hasEntry("jobTitle", "Tuholaistorjuja"))
            ));
    }

    @Test
    public void thatBackgroundUriIsGetCorrectly() throws Exception {
        mockMvc.perform(get(RestConstants.PRIVATE_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(containsString("Profile_")));
    }

    @Test
    public void thatPortfolioIsReturnedWithSharedLink() throws Exception {
        mockMvc.perform(get(String.join("/", RestConstants.PUBLIC_API_V1, SHARED_LINK_PATH, ACTIVE_SHARED_LINK)))
            .andExpect(status().isOk());
    }

    @Test
    public void thatPortfolioIsNotFoundWithExpiredSharedLink() throws Exception {
        mockMvc.perform(get(String.join("/", RestConstants.PUBLIC_API_V1, SHARED_LINK_PATH, EXPIRED_SHARED_LINK)))
            .andExpect(status().isNotFound());
    }
}
