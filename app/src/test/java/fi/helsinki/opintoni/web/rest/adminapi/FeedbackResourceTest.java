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
import fi.helsinki.opintoni.dto.FeedbackDto;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FeedbackResourceTest extends SpringTest {

    private static final Long FEEDBACK_ID = 1L;
    private static final Integer FEEDBACK_ID_INT = 1;
    private static final boolean FEEDBACK_PROCESSED = true;

    @Test
    public void thatFeedbackIsDownloadedAsCsv() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback/csv")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=feedback.csv"))
            .andExpect(content().string("\"Feedback 1\",\"13.10.2015 11:30\",\"teppo.testaaja@helsinki.fi\",1,\"{\"\"browser\"\": \"\"Chrome\"\"}\",false\n" +
                "\"Feedback 2\",\"16.10.2015 12:00\",,2,,false\n"));
    }

    @Test
    public void thatFeedbackIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].content").value("Feedback 1"))
            .andExpect(jsonPath("$[0].email").value("teppo.testaaja@helsinki.fi"))
            .andExpect(jsonPath("$[0].created").value("13.10.2015 11:30"))
            .andExpect(jsonPath("$[0].processed").value(false));
    }

    @Test
    public void thatAccessIsDeniedForNonAdminUser() throws Exception {
        mockMvc.perform(get(RestConstants.ADMIN_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatFeedbackIsUpdated() throws Exception {

        mockMvc.perform(put(RestConstants.ADMIN_API_V1 + "/feedback/" + FEEDBACK_ID)
            .with(securityContext(teacherSecurityContext()))
            .content(WebTestUtils.toJsonBytes(feedbackDto()))
            .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].id").value(FEEDBACK_ID_INT))
            .andExpect(jsonPath("$[0].processed").value(FEEDBACK_PROCESSED));
    }

    private FeedbackDto feedbackDto() {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.id = FEEDBACK_ID;
        feedbackDto.processed = FEEDBACK_PROCESSED;
        return feedbackDto;
    }
}
