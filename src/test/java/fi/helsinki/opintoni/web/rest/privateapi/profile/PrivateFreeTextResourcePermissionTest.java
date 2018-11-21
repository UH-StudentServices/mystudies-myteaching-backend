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
import fi.helsinki.opintoni.dto.profile.FreeTextContentDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateFreeTextResourcePermissionTest extends SpringTest {

    private static final String RESOURCE_PATH_SEGMENT = "freetextcontent";
    private static final String ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME = "4c024239-8dab-4ea0-a686-fe373b040f49";

    private static final Long USERS_PROFILE_ID = 2L;
    private static final Long ANOTHER_USERS_PROFILE_ID = 1L;

    private static final Long ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_ID = 2L;

    @Test
    public void thatUserCannotInsertFreeTextContentItemToAnotherUsersProfile() throws Exception {
        mockMvc.perform(post(resourcePath(ANOTHER_USERS_PROFILE_ID))
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdateFreeTextContentItemInAnotherUsersProfile() throws Exception {
        mockMvc.perform(put(resourcePath(ANOTHER_USERS_PROFILE_ID, ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_ID))
            .content(WebTestUtils.toJsonBytes(new FreeTextContentDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotDeleteFreeTextContentItemPartOfAnotherUsersProfile() throws Exception {
        mockMvc.perform(delete(resourcePath(ANOTHER_USERS_PROFILE_ID, ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_ID))
            .param("instanceName", ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdateFreeTextContentItemSheDoesNowOwn() throws Exception {
        mockMvc.perform(put(resourcePath(USERS_PROFILE_ID, ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_ID))
            .content(WebTestUtils.toJsonBytes(new FreeTextContentDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteFreeTextContentItemSheDoesNowOwn() throws Exception {
        mockMvc.perform(delete(resourcePath(USERS_PROFILE_ID, ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_ID))
            .param("instanceName", ANOTHER_USERS_FREE_TEXT_CONTENT_ITEM_INSTANCE_NAME)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    private String resourcePath(Long profileId) {
        return String.join("/", RestConstants.PRIVATE_API_V1, "profile", profileId.toString(), RESOURCE_PATH_SEGMENT);
    }

    private String resourcePath(Long profileId, Long freeTextContentItemId) {
        return String.join("/", resourcePath(profileId), freeTextContentItemId.toString());
    }
}
