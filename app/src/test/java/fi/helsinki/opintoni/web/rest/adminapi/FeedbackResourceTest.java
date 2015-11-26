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

package fi.helsinki.opintoni.web.rest.adminapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FeedbackResourceTest extends SpringTest {

    @Test
    public void thatFeedbackIsDownloadedAsCsv() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback/csv")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=feedback.csv"))
            .andExpect(content().string("\"Feedback 1\",\"13.10.2015 11:30\",\"teppo.testaaja@helsinki.fi\",\"{\"\"browser\"\": \"\"Chrome\"\"}\"\n" +
                "\"Feedback 2\",\"16.10.2015 12:00\",,\n"));
    }

    @Test
    public void thatFeedbackIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].content").value("Feedback 1"))
            .andExpect(jsonPath("$[0].email").value("teppo.testaaja@helsinki.fi"))
            .andExpect(jsonPath("$[0].created").value("13.10.2015 11:30"));

    }

    @Test
    public void thatAccessIsDeniedForNonAdminUser() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }
}
