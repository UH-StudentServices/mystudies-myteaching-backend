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
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.service.feedback.FeedbackSite;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.SendFeedbackRequest;
import org.junit.Test;
import org.junit.Rule;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.MessagingException;
import java.io.IOException;
import org.springframework.http.MediaType;
import com.google.common.collect.ImmutableMap;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicFeedbackResourceTest extends SpringTest {
    private static final String FEEDBACK_CONTENT = "Content";
    private static final String FEEDBACK_SENDER = "teppo.testaaja@helsinki.fi";
    private static final String FEEDBACK_NO_SENDER = "doo-projekti@helsinki.fi";
    private static final String FEEDBACK_NO_REPLY = "noreply@helsinki.fi";
    private static final String REPLY_TO_HEADER = "Reply-To";
    private static final String FEEDBACK_STUDENT_RECIPIENT = "neu-tuki@helsinki.fi";
    private static final String FEEDBACK_TEACHER_RECIPIENT = "opetusteknologia@helsinki.fi";
    private static final String FEEDBACK_CONTENT_TYPE = "text/plain; charset=UTF-8";
    private static final String FEEDBACK_USER_AGENT = "test-user-agent";
    private static final String FEEDBACK_FACULTY = "H70";
    private static final String FEEDBACK_LANG_FI = "fi";
    private static final String FEEDBACK_LANG_SV = "sv";
    private static final String FEEDBACK_LANG_EN = "en";
    private static final String FEEDBACK_SUBJECT_STUDENT_FI = "Palaute Opintoni-sivulta";
    private static final String FEEDBACK_SUBJECT_TEACHER_FI = "Palaute Opetukseni-sivulta";
    private static final String FEEDBACK_CONTENT_PATTERN_FI =
        "Content\r\n\r\n" +
        "Aikaleima: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
        "Tiedekunta: Valtiotieteellinen tiedekunta\r\n" +
        "Tila: opintoni\r\n" +
        "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_CONTENT_PATTERN_TEACHER_FI =
        "Content\r\n\r\n" +
            "Aikaleima: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
            "Tiedekunta: Valtiotieteellinen tiedekunta\r\n" +
            "Tila: opetukseni\r\n" +
            "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_SUBJECT_SV = "Feedback från Mina Studier websidan";
    private static final String FEEDBACK_CONTENT_PATTERN_SV =
        "Content\r\n\r\n" +
        "Tidsstämpel: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
        "Fakultet: Statsvetenskapliga fakulteten\r\n" +
        "Status: mina studier\r\n" +
        "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_SUBJECT_EN = "Feedback from My Studies web page";
    private static final String FEEDBACK_CONTENT_PATTERN_EN =
        "Content\r\n\r\n" +
        "Timestamp: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
        "Faculty: Faculty of Social Sciences\r\n" +
        "State: my studies\r\n" +
        "User-Agent: test-user-agent\r\n";

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

    @Test
    public void thatFeedbackFromStudentSiteIsSent() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = FEEDBACK_SENDER;
        request.metadata = getMetadata(FEEDBACK_LANG_FI, FeedbackSite.STUDENT);

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(FEEDBACK_STUDENT_RECIPIENT, FEEDBACK_SENDER, null,
            FEEDBACK_SUBJECT_STUDENT_FI, FEEDBACK_CONTENT_PATTERN_FI);
    }

    @Test
    public void thatFeedbackFromTeacherSiteIsSent() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = FEEDBACK_SENDER;
        request.metadata = getMetadata(FEEDBACK_LANG_FI, FeedbackSite.TEACHER);

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(FEEDBACK_TEACHER_RECIPIENT, FEEDBACK_SENDER, null,
            FEEDBACK_SUBJECT_TEACHER_FI, FEEDBACK_CONTENT_PATTERN_TEACHER_FI);
    }

    @Test
    public void thatAnonymousFeedbackIsSent() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = "";
        request.metadata = getMetadata(FEEDBACK_LANG_FI);

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(FEEDBACK_STUDENT_RECIPIENT, FEEDBACK_NO_SENDER, FEEDBACK_NO_REPLY,
            FEEDBACK_SUBJECT_STUDENT_FI, FEEDBACK_CONTENT_PATTERN_FI);
    }

    @Test
    public void thatSwedishFeedbackIsSent() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = FEEDBACK_SENDER;
        request.metadata = getMetadata(FEEDBACK_LANG_SV);

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(FEEDBACK_STUDENT_RECIPIENT, FEEDBACK_SENDER, null,
            FEEDBACK_SUBJECT_SV, FEEDBACK_CONTENT_PATTERN_SV);
    }

    @Test
    public void thatEnglishFeedbackIsSent() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = FEEDBACK_SENDER;
        request.metadata = getMetadata(FEEDBACK_LANG_EN);

        mockMvc.perform(post(RestConstants.PUBLIC_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(FEEDBACK_STUDENT_RECIPIENT, FEEDBACK_SENDER, null,
            FEEDBACK_SUBJECT_EN, FEEDBACK_CONTENT_PATTERN_EN);
    }

    private void checkReceivedMessages(String expectedTo,
                                       String expectedSender,
                                       String expectedReplyTo,
                                       String expectedSubject,
                                       String expectedContentPattern)
            throws MessagingException, IOException {
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        MimeMessage message = messages[0];
        checkBCC(message);
        checkCC(message);
        checkTo(message, expectedTo);
        checkFrom(message, expectedSender);
        checkReplyTo(message, expectedReplyTo);
        checkSubject(message, expectedSubject);
        checkContent(message, expectedContentPattern);
    }

    private void checkBCC(MimeMessage message) throws MessagingException {
        assertThat(message.getRecipients(Message.RecipientType.BCC)).isNull();
    }

    private void checkCC(MimeMessage message) throws MessagingException {
        assertThat(message.getRecipients(Message.RecipientType.CC)).isNull();
    }

    private void checkTo(MimeMessage message, String expectedTo) throws MessagingException {
        Address[] recipients = message.getRecipients(Message.RecipientType.TO);
        assertThat(recipients).hasSize(1);
        assertThat(recipients[0].toString()).isEqualTo(expectedTo);
    }

    private void checkFrom(MimeMessage message, String expectedSender) throws MessagingException {
        Address[] from = message.getFrom();
        assertThat(from).hasSize(1);
        assertThat(from[0].toString()).isEqualTo(expectedSender);
    }

    private void checkReplyTo(MimeMessage message, String expectedReplyTo) throws MessagingException {
        if (expectedReplyTo == null) {
            assertThat(message.getHeader(REPLY_TO_HEADER)).isNull();
        } else {
            Address[] replyTo = message.getReplyTo();
            assertThat(replyTo).hasSize(1);
            assertThat(replyTo[0].toString()).isEqualTo(expectedReplyTo);
        }
    }

    private void checkSubject(MimeMessage message, String expectedSubject) throws MessagingException {
        assertThat(message.getSubject()).isEqualTo(expectedSubject);
    }

    private void checkContent(MimeMessage message, String expectedContentPattern) throws MessagingException, IOException {
        assertThat(message.getContentType()).isEqualTo(FEEDBACK_CONTENT_TYPE);
        assertThat((String)(message.getContent())).matches(expectedContentPattern);
    }

    private JsonNode getMetadata(String lang) {
        return getMetadata(lang, FeedbackSite.STUDENT);
    }

    private JsonNode getMetadata(String lang, FeedbackSite state ) {
        ImmutableMap<String, String> metadata = ImmutableMap.of(
            "userAgent", FEEDBACK_USER_AGENT,
            "faculty", FEEDBACK_FACULTY,
            "state", state.toString(),
            "lang", lang);
        return new ObjectMapper().valueToTree(metadata);
    }
}
