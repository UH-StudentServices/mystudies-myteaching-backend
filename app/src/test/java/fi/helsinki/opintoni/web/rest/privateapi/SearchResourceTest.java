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

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SearchResourceTest extends SpringTest {

    @Test
    public void thatSearchResultsAreReturned() throws Exception {
        leikiServer.expectSearchResults("test", "searchresults.json");

        mockMvc.perform(get("/api/private/v1/search?searchTerm=test").with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("Koulutus ja kasvatus"))
            .andExpect(jsonPath("$[0].link").value("https://helda.helsinki.fi/handle/00/15149"))
            .andExpect(jsonPath("$[0].text").value("Text"))
            .andExpect(jsonPath("$[0].date").isArray())
            .andExpect(jsonPath("$[0].date", hasSize(3)))
            .andExpect(jsonPath("$[0].date[0]").value(2015))
            .andExpect(jsonPath("$[0].date[1]").value(6))
            .andExpect(jsonPath("$[0].date[2]").value(5));
    }

    @Test
    public void thatEmptyResultsAreReturned() throws Exception {
        leikiServer.expectSearchResults("nohits", "emptysearchresults.json");

        mockMvc.perform(get("/api/private/v1/search?searchTerm=nohits").with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void thatCategoriesAreReturned() throws Exception {
        leikiServer.expectCategoryResults("teaching", "categoryresults.json");

        mockMvc.perform(get("/api/private/v1/search/category?searchTerm=teaching")
            .with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].title").value("Teaching 1"));
    }

    @Test
    public void thatEmptyCategoriesAreReturned() throws Exception {
        leikiServer.expectCategoryResults("nohits", "emptycategoryresults.json");

        mockMvc.perform(get("/api/private/v1/search/category?searchTerm=nohits")
            .with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
