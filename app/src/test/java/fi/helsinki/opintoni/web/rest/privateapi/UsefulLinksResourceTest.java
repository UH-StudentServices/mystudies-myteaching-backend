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
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UsefulLinksResourceTest extends SpringTest {
    @Test
    public void thatUsefulLinksReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/private/v1/usefullinks").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$[0].url").value("http://www.google.com"))
            .andExpect(jsonPath("$[0].description").value("Google"))
            .andExpect(jsonPath("$[0].type").value("USER_DEFINED"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$[1].url").value("http://www.helsinki.fi"))
            .andExpect(jsonPath("$[1].description").value("Helsinki University"))
            .andExpect(jsonPath("$[1].type").value("USER_DEFINED"));
    }

    @Test
    public void thatUsefulLinkIsInserted() throws Exception {

        String url = "http://www.helsinki.fi";
        String description = "Helsinki University";

        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = url;
        usefulLinkDto.description = description;

        mockMvc.perform(post("/api/private/v1/usefullinks").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(usefulLinkDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$.url").value(url))
            .andExpect(jsonPath("$.description").value(description))
            .andExpect(jsonPath("$.type").value("USER_DEFINED"));

    }

    @Test
    public void thatUsefulLinkIsDeleted() throws Exception {
        mockMvc.perform(delete("/api/private/v1/usefullinks/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void thatUsefulLinkIsUpdated() throws Exception {
        String url = "new url";
        String description = "New description";

        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = url;
        usefulLinkDto.description = description;

        mockMvc.perform(put("/api/private/v1/usefullinks/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(usefulLinkDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$.url").value(url))
            .andExpect(jsonPath("$.description").value(description))
            .andExpect(jsonPath("$.type").value("USER_DEFINED"));
    }

    @Test
    public void thatUsefulLinkDtoIsValidated() throws Exception {
        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = "";
        usefulLinkDto.description = "";

        mockMvc.perform(put("/api/private/v1/usefullinks/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(usefulLinkDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void thatUsefulLinkOrderIsUpdated() throws Exception {
        OrderUsefulLinksDto orderUsefulLinksDto = new OrderUsefulLinksDto();
        orderUsefulLinksDto.usefulLinkIds = Lists.newArrayList(2L, 1L);

        mockMvc.perform(post("/api/private/v1/usefullinks/order")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(orderUsefulLinksDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[1].id").value(1));
    }
}
