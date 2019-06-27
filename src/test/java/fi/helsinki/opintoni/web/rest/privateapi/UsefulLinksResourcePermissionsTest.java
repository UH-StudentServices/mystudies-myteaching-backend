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

package fi.helsinki.opintoni.web.rest.privateapi;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.OrderUsefulLinksDto;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsefulLinksResourcePermissionsTest extends SpringTest {

    @Test
    public void thatUserCannotUpdateUsefulLinksSheDoesNotOwn() throws Exception {
        UsefulLinkDto usefulLinkDto = createValidUsefulLink();

        mockMvc.perform(put("/api/private/v1/usefullinks/1").with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(usefulLinkDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteUsefulLinksSheDoesNotOwn() throws Exception {
        mockMvc.perform(delete("/api/private/v1/usefullinks/1").with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotReorderUsefulLinksThatSheDoesNotOwn() throws Exception {
        OrderUsefulLinksDto orderUsefulLinksDto = new OrderUsefulLinksDto();
        orderUsefulLinksDto.usefulLinkIds = Lists.newArrayList(1L, 2L, 3L);

        mockMvc.perform(post("/api/private/v1/usefullinks/order").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(orderUsefulLinksDto)))
            .andExpect(status().isForbidden());
    }

    private UsefulLinkDto createValidUsefulLink() {
        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = "url";
        usefulLinkDto.description = "description";
        return usefulLinkDto;
    }
}
