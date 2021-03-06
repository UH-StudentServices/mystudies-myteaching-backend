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
import fi.helsinki.opintoni.dto.profile.ProfileSharedLinkDto;
import fi.helsinki.opintoni.repository.profile.ProfileSharedLinkRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateProfileSharedLinksResourceTest extends SpringTest {

    private static final Long PROFILE_ID = 2L;
    private static final String RESOURCE_URL = "/api/private/v1/profile/" + PROFILE_ID + "/sharedlinks";

    @Autowired
    private ProfileSharedLinkRepository profileSharedLinkRepository;

    @Test
    public void thatSharedLinkIsCreated() throws Exception {
        mockMvc.perform(post(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(new ProfileSharedLinkDto()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sharedLinkFragment").isNotEmpty());
    }

    @Test
    public void thatSharedLinksAreFetched() throws Exception {
        mockMvc.perform(get(RESOURCE_URL)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].sharedLinkFragment").value("a3728b39-7099-4f8c-9413-da2817eeccf9"));
    }

    @Test
    public void thatSharedLinksIsDeleted() throws Exception {
        mockMvc.perform(delete(RESOURCE_URL + "/1")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());

        assertThat(profileSharedLinkRepository.findById(1L).isPresent()).isFalse();
    }
}
