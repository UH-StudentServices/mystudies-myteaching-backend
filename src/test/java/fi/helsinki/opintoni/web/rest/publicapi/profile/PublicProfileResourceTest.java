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

import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.dto.profile.ComponentHeadingDto;
import fi.helsinki.opintoni.dto.profile.ComponentOrderDto;
import fi.helsinki.opintoni.dto.profile.FreeTextContentDto;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicProfileResourceTest extends PublicProfileTest {

    private static final String STUDENT_PROFILE_PATH = "/profile/student/en/olli-opiskelija";
    private static final String TEACHER_PROFILE_PATH = "/profile/teacher/fi/opettaja";

    private static final String PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME = "4c024239-8dab-4ea0-a686-fe373b040f48";

    private static final String SHARED_LINK_PATH = "/profile/shared";
    private static final String ACTIVE_SHARED_LINK = "/a3728b39-7099-4f8c-9413-da2817eeccf9";
    private static final String EXPIRED_SHARED_LINK = "/b2672af7-306f-43aa-ab3f-acbc6a41f47f";

    @Autowired
    private UserSettingsService userSettingsService;

    @Test
    public void thatProfileIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.avatarUrl").value(
                ABSOLUTE_PUBLIC_API_PATH + STUDENT_PROFILE_PATH + PROFILE_IMAGE))
            .andExpect(jsonPath("$.id").value(STUDENT_PROFILE_ID));
    }

    @Test
    public void thatStudentProfileContainsNoLinkedPrivateComponents() throws Exception {
        setPrivateVisibilityForEveryStudentProfileComponent();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH)
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
    public void thatStudentProfileContainsLinkedPublicComponentItems() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.degrees").isArray())
            .andExpect(jsonPath("$.degrees", hasSize(1)))
            .andExpect(jsonPath("$.workExperience").isArray())
            .andExpect(jsonPath("$.workExperience", hasSize(3)))
            .andExpect(jsonPath("$.languageProficiencies").isArray())
            .andExpect(jsonPath("$.languageProficiencies", hasSize(3)))
            .andExpect(jsonPath("$.samples").isArray())
            .andExpect(jsonPath("$.samples", hasSize(1)));
    }

    @Test
    public void thatStudentProfileContainsNoLinkedPrivateComponentItems() throws Exception {
        setPrivateVisibilityForEveryStudentProfileComponentItem();
        saveStudentProfileAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.degrees").isEmpty())
            .andExpect(jsonPath("$.workExperience").isEmpty())
            .andExpect(jsonPath("$.languageProficiencies").isEmpty())
            .andExpect(jsonPath("$.samples").isEmpty());
    }

    @Test
    public void thatTeacherProfileDoesNotContainComponentsLinkedToPrivateSections() throws Exception {
        saveTeacherProfileAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PROFILE_PATH)
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
    public void thatPrivateProfileIsNotFoundFromPublicApi() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatPublicTeacherProfileContainsPublicContactInformation() throws Exception {
        saveTeacherProfileAsPublic();

        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contactInformation.email")
                .value("olli.opettaja@helsinki.fi"));
    }

    @Test
    public void thatStudentProfileContainsComponentOrderInfo() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.componentOrders").value(Matchers.<List<ComponentOrderDto>>allOf(
                hasSize(3),
                hasItem(
                    both(hasEntry("component", ProfileComponent.STUDIES.toString()))
                        .and(hasEntry("orderValue", 1))
                ),
                hasItem(
                    both(hasEntry("component", ProfileComponent.DEGREES.toString()))
                        .and(hasEntry("orderValue", 2))
                ),
                hasItem(
                    both(hasEntry("component", ProfileComponent.ATTAINMENTS.toString()))
                        .and(hasEntry("orderValue", 3))
                )
            )));
    }

    @Test
    public void thatStudentProfileOnlyContainsPublicFreeTextContentItems() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    hasEntry("instanceName", PUBLIC_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME)
                )
            )));
    }

    @Test
    public void thatAllHeadingsForPublicComponentsAreReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.headings").value(Matchers.<List<ComponentHeadingDto>>allOf(
                hasSize(2),
                hasItem(
                    both(hasEntry("component", ProfileComponent.STUDIES.toString()))
                        .and(hasEntry("heading", "Test heading"))
                ),
                hasItem(
                    both(hasEntry("component", ProfileComponent.DEGREES.toString()))
                        .and(hasEntry("heading", "Another heading"))
                )
            )));
    }

    @Test
    public void thatStudentProfileWorkExperiencesAreOrderedProperly() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH)
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
        mockMvc.perform(get(RestConstants.PRIVATE_API_V1 + STUDENT_PROFILE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(containsString("Profile_")));
    }

    @Test
    public void thatProfileIsReturnedWithSharedLink() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + SHARED_LINK_PATH + ACTIVE_SHARED_LINK))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.avatarUrl").value(
               ABSOLUTE_PUBLIC_API_PATH + SHARED_LINK_PATH + ACTIVE_SHARED_LINK + PROFILE_IMAGE
            ));
    }

    @Test
    public void thatProfileIsNotFoundWithExpiredSharedLink() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1, SHARED_LINK_PATH, EXPIRED_SHARED_LINK))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatPublicProfileImageIsFound() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + STUDENT_PROFILE_PATH + PROFILE_IMAGE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    public void thatPrivateProfileImageIsNotFound() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + TEACHER_PROFILE_PATH + PROFILE_IMAGE))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void thatSharedProfileImageIsFound() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + SHARED_LINK_PATH + ACTIVE_SHARED_LINK + PROFILE_IMAGE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    public void thatSharedProfileImageIsNotFoundWithExpiredLink() throws Exception {
        mockMvc.perform(get(RestConstants.PUBLIC_API_V1 + SHARED_LINK_PATH + EXPIRED_SHARED_LINK + PROFILE_IMAGE))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
