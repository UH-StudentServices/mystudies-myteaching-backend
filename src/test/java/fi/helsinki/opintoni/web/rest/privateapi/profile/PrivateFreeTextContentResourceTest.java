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
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.dto.profile.FreeTextContentDto;
import fi.helsinki.opintoni.repository.profile.ComponentVisibilityRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateFreeTextContentResourceTest extends SpringTest {

    private static final String RESOURCE_PATH_SEGMENT = "freetextcontent";

    private static final String NEW_TITLE = "Uusi otsikko";
    private static final String NEW_TEXT = "Uusi teksti";
    private static final String MANUALLY_ASSIGNED_INSTANCE_NAME = "Testi-ID";
    private static final String GENERATED_INSTANCE_NAME = "4c024239-8dab-4ea0-a686-fe373b040f48";

    private static final Long STUDENT_PROFILE_ID = 2L;
    private static final Long TEACHER_PROFILE_ID = 4L;
    private static final Long FREE_TEXT_CONTENT_ITEM_ID = 1L;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Test
    public void thatFreeTextContentItemIsInsertedWithGeneratedInstanceName() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = NEW_TITLE;
        freeTextContentDto.text = NEW_TEXT;

        mockMvc.perform(post(resourcePath(STUDENT_PROFILE_ID))
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(NEW_TITLE))
            .andExpect(jsonPath("$.text").value(NEW_TEXT))
            .andExpect(jsonPath("$.instanceName").isString());
    }

    @Test
    public void that422IsReturnedWhenInsertingFreeTextContentItemWithEmptyValue() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = "";
        freeTextContentDto.text = NEW_TEXT;

        mockMvc.perform(post(resourcePath(STUDENT_PROFILE_ID))
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void thatSectionBoundFreeTextContentItemIsInsertedWithExistingInstanceName() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = NEW_TITLE;
        freeTextContentDto.text = NEW_TEXT;
        freeTextContentDto.profileSection = TeacherProfileSection.BASIC_INFORMATION.toString();
        freeTextContentDto.instanceName = MANUALLY_ASSIGNED_INSTANCE_NAME;

        mockMvc.perform(post(resourcePath(TEACHER_PROFILE_ID))
            .with(securityContext(teacherSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(NEW_TITLE))
            .andExpect(jsonPath("$.text").value(NEW_TEXT))
            .andExpect(jsonPath("$.profileSection").value(TeacherProfileSection.BASIC_INFORMATION.toString()))
            .andExpect(jsonPath("$.instanceName").value(MANUALLY_ASSIGNED_INSTANCE_NAME));
    }

    @Test
    public void thatFreeTextContentItemIsUpdated() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = NEW_TITLE;
        freeTextContentDto.text = NEW_TEXT;

        mockMvc.perform(put(resourcePath(STUDENT_PROFILE_ID, FREE_TEXT_CONTENT_ITEM_ID))
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(NEW_TITLE))
            .andExpect(jsonPath("$.text").value(NEW_TEXT));
    }

    @Test
    public void that422IsReturnedWhenUpdatingFreeTextContentItemWithEmptyValue() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = "";
        freeTextContentDto.text = NEW_TEXT;

        mockMvc.perform(put(resourcePath(STUDENT_PROFILE_ID, FREE_TEXT_CONTENT_ITEM_ID))
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void thatFreeTextContentItemIsDeletedAlongWithMatchingComponentVisibility() throws Exception {
        mockMvc.perform(delete(resourcePath(STUDENT_PROFILE_ID, FREE_TEXT_CONTENT_ITEM_ID))
            .param("instanceName", GENERATED_INSTANCE_NAME)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());

        assertThat(componentVisibilityRepository
            .findByProfileIdAndComponentAndTeacherProfileSectionAndInstanceName(
                STUDENT_PROFILE_ID,
                ProfileComponent.FREE_TEXT_CONTENT,
                null,
                GENERATED_INSTANCE_NAME
            ).isPresent()).isFalse();
    }

    private String resourcePath(Long profileId) {
        return String.join("/", RestConstants.PRIVATE_API_V1, "profile", profileId.toString(), RESOURCE_PATH_SEGMENT);
    }

    private String resourcePath(Long profileId, Long freeTextContentItemId) {
        return String.join("/", resourcePath(profileId), freeTextContentItemId.toString());
    }
}
