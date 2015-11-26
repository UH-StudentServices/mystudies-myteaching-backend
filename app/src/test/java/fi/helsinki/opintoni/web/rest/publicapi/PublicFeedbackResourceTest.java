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

package fi.helsinki.opintoni.web.rest.publicapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.Feedback;
import fi.helsinki.opintoni.repository.FeedbackRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.InsertFeedbackRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicFeedbackResourceTest extends SpringTest {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Test
    public void thatFeedbackIsInserted() throws Exception {
        InsertFeedbackRequest request = new InsertFeedbackRequest();
        request.content = "Content";
        request.email = "teppo.testaaja@helsinki.fi";
        request.metadata = getMetadata();

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(3, feedbackRepository.findAll().size());

        Feedback feedback = feedbackRepository.findAll().get(2);

        assertEquals(request.content, feedback.content);
        assertEquals(request.email, feedback.email);
        assertEquals("{\"browser\":\"Chrome\"}", feedback.metadata);
    }

    private JsonNode getMetadata() {
        Map<String, String> metadata = Maps.newHashMap();
        metadata.put("browser", "Chrome");
        return new ObjectMapper().valueToTree(metadata);
    }
}
