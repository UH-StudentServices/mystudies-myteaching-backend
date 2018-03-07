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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.service.feedback.FeedbackSite;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateFeedbackResourceTest extends SpringTest {
    private static final String FEEDBACK_CONTENT = "Content";
    private static final String FEEDBACK_SENDER = "teppo.testaaja@helsinki.fi";

    @Value("${feedback.anonymous.fromAddress}")
    private String feedbackNoSender;

    @Value("${feedback.anonymous.replyToAddress}")
    private String feedbackNoReply;

    private static final String REPLY_TO_HEADER = "Reply-To";

    @Value("${feedback.recipient.student}")
    private String feedbackStudentRecipient;

    @Value("${feedback.recipient.teacher}")
    private String feedbackTeacherRecipient;

    @Value("${feedback.recipient.portfolio}")
    private String feedbackPortfolioRecipient;

    @Value("${feedback.recipient.academicPortfolio}")
    private String feedbackAcademicPortfolioRecipient;

    private static final String FEEDBACK_CONTENT_TYPE = "text/plain; charset=UTF-8";
    private static final String FEEDBACK_USER_AGENT = "test-user-agent";

    private static final String FEEDBACK_FACULTY = "H70";
    private static final String FEEDBACK_LANG_FI = "fi";
    private static final String FEEDBACK_LANG_SV = "sv";
    private static final String FEEDBACK_LANG_EN = "en";

    private static final String FEEDBACK_SUBJECT_MY_STUDIES_FI = "Palaute Opintoni-sivulta";
    private static final String FEEDBACK_SUBJECT_MY_STUDIES_SV = "Feedback från Mina Studier websidan";
    private static final String FEEDBACK_SUBJECT_MY_STUDIES_EN = "Feedback from My Studies web page";

    private static final String FEEDBACK_SUBJECT_MY_TEACHING_FI = "Palaute Opetukseni-sivulta";
    private static final String FEEDBACK_SUBJECT_MY_TEACHING_SV = "Feedback från Min Undervisning websidan";
    private static final String FEEDBACK_SUBJECT_MY_TEACHING_EN = "Feedback from My Teaching web page";

    private static final String FEEDBACK_SUBJECT_PORTFOLIO_FI = "Palaute portfoliosta";
    private static final String FEEDBACK_SUBJECT_PORTFOLIO_SV = "Feedback från portfölj";
    private static final String FEEDBACK_SUBJECT_PORTFOLIO_EN = "Feedback from portfolio";

    private static final String FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_FI = "Palaute yliopistoportfoliosta";
    private static final String FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_SV = "Feedback från universitetsportfölj";
    private static final String FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_EN = "Feedback from academic portfolio";

    private static final String SITE_MY_STUDIES_FI = "Opintoni";
    private static final String SITE_MY_STUDIES_SV = "Mina Studier";
    private static final String SITE_MY_STUDIES_EN = "My Studies";

    private static final String SITE_MY_TEACHING_FI = "Opetukseni";
    private static final String SITE_MY_TEACHING_SV = "Min Undervisning";
    private static final String SITE_MY_TEACHING_EN = "My Teaching";

    private static final String SITE_PORTFOLIO_FI = "Portfolio";
    private static final String SITE_PORTFOLIO_SV = "Portfölj";
    private static final String SITE_PORTFOLIO_EN = "Portfolio";

    private static final String SITE_ACADEMIC_PORTFOLIO_FI = "Yliopistoportfolio";
    private static final String SITE_ACADEMIC_PORTFOLIO_SV = "Universitetsportfölj";
    private static final String SITE_ACADEMIC_PORTFOLIO_EN = "Academic Portfolio";

    private static final String FEEDBACK_CONTENT_PATTERN_FI =
        "Content\r\n\r\n" +
        "Aikaleima: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
        "Tiedekunta: Valtiotieteellinen tiedekunta\r\n" +
        "Palvelu: %s\r\n" +
        "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_CONTENT_PATTERN_SV =
        "Content\r\n\r\n" +
            "Tidsstämpel: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
            "Fakultet: Statsvetenskapliga fakulteten\r\n" +
            "Websidan: %s\r\n" +
            "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_CONTENT_PATTERN_EN =
        "Content\r\n\r\n" +
            "Timestamp: \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d\r\n" +
            "Faculty: Faculty of Social Sciences\r\n" +
            "Site: %s\r\n" +
            "User-Agent: test-user-agent\r\n";
    private static final String FEEDBACK_CONTENT_PATTERN_MY_STUDIES_FI = String.format(FEEDBACK_CONTENT_PATTERN_FI, SITE_MY_STUDIES_FI);
    private static final String FEEDBACK_CONTENT_PATTERN_MY_STUDIES_SV = String.format(FEEDBACK_CONTENT_PATTERN_SV, SITE_MY_STUDIES_SV);
    private static final String FEEDBACK_CONTENT_PATTERN_MY_STUDIES_EN = String.format(FEEDBACK_CONTENT_PATTERN_EN, SITE_MY_STUDIES_EN);

    private static final String FEEDBACK_CONTENT_PATTERN_MY_TEACHING_FI = String.format(FEEDBACK_CONTENT_PATTERN_FI, SITE_MY_TEACHING_FI);
    private static final String FEEDBACK_CONTENT_PATTERN_MY_TEACHING_SV = String.format(FEEDBACK_CONTENT_PATTERN_SV, SITE_MY_TEACHING_SV);
    private static final String FEEDBACK_CONTENT_PATTERN_MY_TEACHING_EN = String.format(FEEDBACK_CONTENT_PATTERN_EN, SITE_MY_TEACHING_EN);

    private static final String FEEDBACK_CONTENT_PATTERN_PORTFOLIO_FI = String.format(FEEDBACK_CONTENT_PATTERN_FI, SITE_PORTFOLIO_FI);
    private static final String FEEDBACK_CONTENT_PATTERN_PORTFOLIO_SV = String.format(FEEDBACK_CONTENT_PATTERN_SV, SITE_PORTFOLIO_SV);
    private static final String FEEDBACK_CONTENT_PATTERN_PORTFOLIO_EN = String.format(FEEDBACK_CONTENT_PATTERN_EN, SITE_PORTFOLIO_EN);

    private static final String FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_FI =
        String.format(FEEDBACK_CONTENT_PATTERN_FI, SITE_ACADEMIC_PORTFOLIO_FI);
    private static final String FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_SV =
        String.format(FEEDBACK_CONTENT_PATTERN_SV, SITE_ACADEMIC_PORTFOLIO_SV);
    private static final String FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_EN =
        String.format(FEEDBACK_CONTENT_PATTERN_EN, SITE_ACADEMIC_PORTFOLIO_EN);

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

    private void thatFeedbackIsSent(String state,
                                   String lang,
                                   String recipientEmail,
                                   String subject,
                                   String contentPattern) throws Exception {
        thatFeedbackIsSent(state, lang, recipientEmail, subject, contentPattern, FEEDBACK_SENDER, FEEDBACK_SENDER, null);
    }

    private void thatFeedbackIsSent(String site,
                                    String lang,
                                    String recipientEmail,
                                    String subject,
                                    String contentPattern,
                                    String requestFromEmail,
                                    String receivedMessageFromEmail,
                                    String replyToEmail) throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = requestFromEmail;
        request.metadata = getMetadata(lang, site);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        checkReceivedMessages(
            recipientEmail,
            receivedMessageFromEmail,
            replyToEmail,
            subject,
            contentPattern);
    }

    @Test
    public void thatFeedbackFromMyStudiesIsSentInFinnish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.STUDENT.toString(),
            FEEDBACK_LANG_FI,
            feedbackStudentRecipient,
            FEEDBACK_SUBJECT_MY_STUDIES_FI,
            FEEDBACK_CONTENT_PATTERN_MY_STUDIES_FI);
    }

    @Test
    public void thatFeedbackFromMyStudiesIsSentInSwedish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.STUDENT.toString(),
            FEEDBACK_LANG_SV,
            feedbackStudentRecipient,
            FEEDBACK_SUBJECT_MY_STUDIES_SV,
            FEEDBACK_CONTENT_PATTERN_MY_STUDIES_SV);
    }

    @Test
    public void thatFeedbackFromMyStudiesIsSentInEnglish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.STUDENT.toString(),
            FEEDBACK_LANG_EN,
            feedbackStudentRecipient,
            FEEDBACK_SUBJECT_MY_STUDIES_EN,
            FEEDBACK_CONTENT_PATTERN_MY_STUDIES_EN);
    }

    @Test
    public void thatFeedbackFromMyTeachingIsSentInFinnish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.TEACHER.toString(),
            FEEDBACK_LANG_FI,
            feedbackTeacherRecipient,
            FEEDBACK_SUBJECT_MY_TEACHING_FI,
            FEEDBACK_CONTENT_PATTERN_MY_TEACHING_FI);
    }

    @Test
    public void thatFeedbackFromMyTeachingIsSentInSwedish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.TEACHER.toString(),
            FEEDBACK_LANG_SV,
            feedbackTeacherRecipient,
            FEEDBACK_SUBJECT_MY_TEACHING_SV,
            FEEDBACK_CONTENT_PATTERN_MY_TEACHING_SV);
    }

    @Test
    public void thatFeedbackFromMyTeachingIsSentInEnglish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.TEACHER.toString(),
            FEEDBACK_LANG_EN,
            feedbackTeacherRecipient,
            FEEDBACK_SUBJECT_MY_TEACHING_EN,
            FEEDBACK_CONTENT_PATTERN_MY_TEACHING_EN);
    }

    @Test
    public void thatFeedbackFromPortfolioIsSentInFinnish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.PORTFOLIO.toString(),
            FEEDBACK_LANG_FI,
            feedbackPortfolioRecipient,
            FEEDBACK_SUBJECT_PORTFOLIO_FI,
            FEEDBACK_CONTENT_PATTERN_PORTFOLIO_FI);
    }

    @Test
    public void thatFeedbackFromPortfolioIsSentInSwedish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.PORTFOLIO.toString(),
            FEEDBACK_LANG_SV,
            feedbackPortfolioRecipient,
            FEEDBACK_SUBJECT_PORTFOLIO_SV,
            FEEDBACK_CONTENT_PATTERN_PORTFOLIO_SV);
    }

    @Test
    public void thatFeedbackFromPortfolioIsSentInEnglish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.PORTFOLIO.toString(),
            FEEDBACK_LANG_EN,
            feedbackPortfolioRecipient,
            FEEDBACK_SUBJECT_PORTFOLIO_EN,
            FEEDBACK_CONTENT_PATTERN_PORTFOLIO_EN);
    }

    @Test
    public void thatFeedbackFromAcademicPortfolioIsSentInFinnish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.ACADEMIC_PORTFOLIO.toString(),
            FEEDBACK_LANG_FI,
            feedbackAcademicPortfolioRecipient,
            FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_FI,
            FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_FI);
    }

    @Test
    public void thatFeedbackFromAcademicPortfolioIsSentInSwedish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.ACADEMIC_PORTFOLIO.toString(),
            FEEDBACK_LANG_SV,
            feedbackAcademicPortfolioRecipient,
            FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_SV,
            FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_SV);
    }

    @Test
    public void thatFeedbackFromAcademicPortfolioIsSentInEnglish() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.ACADEMIC_PORTFOLIO.toString(),
            FEEDBACK_LANG_EN,
            feedbackAcademicPortfolioRecipient,
            FEEDBACK_SUBJECT_ACADEMIC_PORTFOLIO_EN,
            FEEDBACK_CONTENT_PATTERN_ACADEMIC_PORTFOLIO_EN);
    }


    @Test
    public void thatAnonymousFeedbackIsSent() throws Exception {
        thatFeedbackIsSent(
            FeedbackSite.STUDENT.toString(),
            FEEDBACK_LANG_FI,
            feedbackStudentRecipient,
            FEEDBACK_SUBJECT_MY_STUDIES_FI,
            FEEDBACK_CONTENT_PATTERN_MY_STUDIES_FI,
            "",
            feedbackNoSender,
            feedbackNoReply);
    }


    @Test
    public void thatInvalidMessageStateCausesError() throws Exception {
        SendFeedbackRequest request = new SendFeedbackRequest();
        request.content = FEEDBACK_CONTENT;
        request.email = FEEDBACK_SENDER;
        request.metadata = getMetadata(FEEDBACK_LANG_FI, "kukkusteitti");

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + "/feedback")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
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

    private JsonNode getMetadata(String lang, String site) {
        ImmutableMap<String, String> metadata = ImmutableMap.of(
            "userAgent", FEEDBACK_USER_AGENT,
            "faculty", FEEDBACK_FACULTY,
            "site", site,
            "lang", lang);
        return new ObjectMapper().valueToTree(metadata);
    }
}
