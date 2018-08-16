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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.degree.UpdateDegree;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static com.google.common.collect.Lists.newArrayList;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateDegreeResourceTest extends SpringTest {

    @Test
    public void thatDegreesAreUpdated() throws Exception {
        UpdateDegree updateDegree = new UpdateDegree();
        updateDegree.title = "Degree Title";
        updateDegree.institution = "University of Helsinki";
        updateDegree.description = "Degree description";
        updateDegree.dateOfDegree = LocalDate.of(2016, 6, 6);

        mockMvc.perform(post("/api/private/v1/portfolio/2/degree")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(newArrayList(updateDegree)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Degree Title"))
            .andExpect(jsonPath("$[0].institution").value("University of Helsinki"))
            .andExpect(jsonPath("$[0].description").value("Degree description"))
            .andExpect(jsonPath("$[0].dateOfDegree[0]").value(2016))
            .andExpect(jsonPath("$[0].dateOfDegree[1]").value(6))
            .andExpect(jsonPath("$[0].dateOfDegree[2]").value(6));
    }

    @Test
    public void thatDegreesAreDeleted() throws Exception {
        mockMvc.perform(post("/api/private/v1/portfolio/2/degree")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(newArrayList()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
