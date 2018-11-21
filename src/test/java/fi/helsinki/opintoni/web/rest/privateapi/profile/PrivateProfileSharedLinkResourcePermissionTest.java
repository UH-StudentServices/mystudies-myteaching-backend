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
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateProfileSharedLinkResourcePermissionTest extends SpringTest {

    private static final Long PROFILE_ID = 3L;
    private static final String RESOURCE_URL = "/api/private/v1/profile/" + PROFILE_ID + "/sharedlinks";

    @Test
    public void thatUserCantAddSharedLinkToProfileSheDoesntOwn() throws Exception {
        mockMvc.perform(post(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(new ProfileSharedLinkDto()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCantGetSharedLinksSheDoesntOwn() throws Exception {
        mockMvc.perform(get(RESOURCE_URL).with(securityContext(studentSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCantRemoveSharedLinkSheDoesntOwn() throws Exception {
        mockMvc.perform(delete(RESOURCE_URL + "/2").with(securityContext(studentSecurityContext())))
            .andExpect(status().isNotFound());
    }
}
