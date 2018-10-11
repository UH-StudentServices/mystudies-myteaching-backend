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

package fi.helsinki.opintoni.service.feedback;

import com.fasterxml.jackson.databind.JsonNode;
import fi.helsinki.opintoni.exception.http.BadRequestException;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.TimeService;
import fi.helsinki.opintoni.web.rest.privateapi.SendFeedbackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Transactional
public class FeedbackService {
    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String DEFAULT_LANG = Language.FI.getCode();
    private static final String COUNTRY_FINLAND_CODE = "FI";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String FACULTY_FIELD = "faculty";
    private static final String SITE_FIELD = "site";
    private static final String USER_AGENT_FIELD = "userAgent";
    private static final String LANG_FIELD = "lang";
    private static final String DEFAULT_SUBJECT = "Palaute";

    private final MailSender mailSender;
    private final MessageSource messageSource;
    private final String studentFeedbackToAddress;
    private final String teacherFeedbackToAddress;
    private final String portfolioFeedbackToAddress;
    private final String academicPortfolioFeedbackToAddress;
    private final String anonymousFeedbackFromAddress;
    private final String anonymousFeedbackReplyToAddress;

    @Autowired
    public FeedbackService(MailSender mailSender,
                           MessageSource messageSource,
                           @Value("${feedback.recipient.student}") String studentFeedbackToAddress,
                           @Value("${feedback.recipient.teacher}") String teacherFeedbackToAddress,
                           @Value("${feedback.recipient.portfolio}") String portfolioFeedbackToAddress,
                           @Value("${feedback.recipient.academicPortfolio}") String academicPortfolioFeedbackToAddress,
                           @Value("${feedback.anonymous.fromAddress}") String anonymousFeedbackFromAddress,
                           @Value("${feedback.anonymous.replyToAddress}") String anonymousFeedbackReplyToAddress) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.studentFeedbackToAddress = studentFeedbackToAddress;
        this.teacherFeedbackToAddress = teacherFeedbackToAddress;
        this.portfolioFeedbackToAddress = portfolioFeedbackToAddress;
        this.academicPortfolioFeedbackToAddress = academicPortfolioFeedbackToAddress;
        this.anonymousFeedbackFromAddress = anonymousFeedbackFromAddress;
        this.anonymousFeedbackReplyToAddress = anonymousFeedbackReplyToAddress;
    }

    public void sendFeedback(SendFeedbackRequest request) throws MailException {
        String content = request.content;
        String timestamp = createTimestamp();
        String site = getSite(request);
        String faculty = getFaculty(request);
        String userAgent = getUserAgent(request);
        Locale locale = getLocale(request);
        final String body = buildMessageBody(content, timestamp, faculty, site, userAgent, locale);

        SimpleMailMessage message = new SimpleMailMessage();
        if ((request.email == null) || request.email.isEmpty()) {
            message.setFrom(anonymousFeedbackFromAddress);
            message.setReplyTo(anonymousFeedbackReplyToAddress);
        } else {
            message.setFrom(request.email);
        }

        if (FeedbackSite.STUDENT.equalsName(site)) {
            message.setTo(studentFeedbackToAddress);
        } else if (FeedbackSite.TEACHER.equalsName(site)) {
            message.setTo(teacherFeedbackToAddress);
        } else if (FeedbackSite.PORTFOLIO.equalsName(site)) {
            message.setTo(portfolioFeedbackToAddress);
        } else if (FeedbackSite.ACADEMIC_PORTFOLIO.equalsName(site)) {
            message.setTo(academicPortfolioFeedbackToAddress);
        } else {
            log.error("Unexpected message state: {}", site);
            throw new BadRequestException("Unexpected message metadata state");
        }

        message.setSubject(messageSource.getMessage("feedback.subject." + site, null, DEFAULT_SUBJECT, locale));

        message.setText(body);
        mailSender.send(message);
    }

    private String createTimestamp() {
        ZonedDateTime helsinkiNow = ZonedDateTime.now(TimeService.HELSINKI_ZONE_ID);
        return helsinkiNow.format(TIMESTAMP_FORMATTER);
    }

    private String getSite(SendFeedbackRequest request) {
        return getMetadataValue(request, SITE_FIELD, null);
    }

    private String getFaculty(SendFeedbackRequest request) {
        return getMetadataValue(request, FACULTY_FIELD, null);
    }

    private String getUserAgent(SendFeedbackRequest request) {
        return getMetadataValue(request, USER_AGENT_FIELD, null);
    }

    private Locale getLocale(SendFeedbackRequest request) {
        String langCode = getMetadataValue(request, LANG_FIELD, DEFAULT_LANG);
        return new Locale(langCode, COUNTRY_FINLAND_CODE);
    }

    private String getMetadataValue(SendFeedbackRequest request, String key, String defaultValue) {
        JsonNode node = request.metadata.findValue(key);
        return node == null ? defaultValue : node.textValue();
    }

    private String buildMessageBody(String content,
                                    String timestamp,
                                    String faculty,
                                    String state,
                                    String userAgent,
                                    Locale locale) {
        String timestampLine = getTimestampLine(timestamp, locale);
        String stateLine = getSiteLine(state, locale);
        String userAgentLine = getUserAgentLine(userAgent, locale);
        if (faculty == null) {
            return String.join("\n", content, "", timestampLine, stateLine, userAgentLine);
        } else {
            String facultyLine = getFacultyLine(faculty, locale);
            return String.join("\n", content, "", timestampLine, facultyLine, stateLine, userAgentLine);
        }
    }

    private String getTimestampLine(String timestamp, Locale locale) {
        return getNameValueLine(TIMESTAMP_FIELD, timestamp, locale);
    }

    private String getFacultyLine(String faculty, Locale locale) {
        return getNameValueLine(FACULTY_FIELD, messageSource.getMessage("faculty." + faculty, null, faculty, locale), locale);
    }

    private String getSiteLine(String site, Locale locale) {
        return getNameValueLine(SITE_FIELD, messageSource.getMessage("site." + site, null, site, locale), locale);
    }

    private String getUserAgentLine(String userAgent, Locale locale) {
        return getNameValueLine(USER_AGENT_FIELD, userAgent, locale);
    }

    private String getNameValueLine(String name, String value, Locale locale) {
        return messageSource.getMessage("feedback.label." + name, null, locale) + ": " + value;
    }
}
