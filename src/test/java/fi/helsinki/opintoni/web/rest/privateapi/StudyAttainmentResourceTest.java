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
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.*;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudyAttainmentResourceTest extends SpringTest {

    @Test
    public void getStudyAttainmentsReturnsCorrectResponse() throws Exception {
        defaultStudentRequestChain().attainments();

        mockMvc.perform(get("/api/private/v1/studyattainments").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].learningOpportunityName").value(LEARNING_OPPORTINITY_NAME))
            .andExpect(jsonPath("$[0].attainmentDate").isArray())
            .andExpect(jsonPath("$[0].attainmentDate[0]").value(ATTAINMENT_DATE_YEAR))
            .andExpect(jsonPath("$[0].attainmentDate[1]").value(ATTAINMENT_DATE_MONTH))
            .andExpect(jsonPath("$[0].attainmentDate[2]").value(ATTAINMENT_DATE_DAY))
            .andExpect(jsonPath("$[0].attainmentDate[3]").value(ATTAINMENT_DATE_HOUR))
            .andExpect(jsonPath("$[0].attainmentDate[4]").value(ATTAINMENT_DATE_MINUTE))
            .andExpect(jsonPath("$[0].grade").value(GRADE))
            .andExpect(jsonPath("$[0].credits").value(CREDITS))
            .andExpect(jsonPath("$[0].teachers").isArray())
            .andExpect(jsonPath("$[0].teachers[0].name").value(TEACHER_NAME));
    }

}
