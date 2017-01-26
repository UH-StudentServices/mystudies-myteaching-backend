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

package fi.helsinki.opintoni.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import fi.helsinki.opintoni.domain.Feedback;
import fi.helsinki.opintoni.dto.FeedbackDto;
import fi.helsinki.opintoni.repository.FeedbackRepository;
import fi.helsinki.opintoni.service.converter.FeedbackConverter;
import fi.helsinki.opintoni.web.rest.privateapi.InsertFeedbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Service
@Transactional
public class FeedbackService {
    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    private static final ZoneId HELSINKI_ZONE_ID = ZoneId.of("Europe/Helsinki");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Map<String, Locale> CODES_TO_LOCALES = new HashMap<>();
    static {
        CODES_TO_LOCALES.put("fi", new Locale("fi", "FI"));
        CODES_TO_LOCALES.put("sv", new Locale("sv", "FI"));
        CODES_TO_LOCALES.put("en", new Locale("en", "FI"));
    }

    private final MailSender mailSender;
    private final MessageSource messageSource;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackConverter feedbackConverter;
    private final String efecteEmailAddress;
    private final String anonymousFeedbackFromAddress;
    private final String anonymousFeedbackReplyToAddress;


    @Autowired
    public FeedbackService(MailSender mailSender,
                           MessageSource messageSource,
                           @Value("${feedback.efecte.address}") String efecteEmailAddress,
                           @Value("${feedback.anonymous.fromAddress}") String anonymousFeedbackFromAddress,
                           @Value("${feedback.anonymous.replyToAddress}") String anonymousFeedbackReplyToAddress,
                           FeedbackRepository feedbackRepository,
                           FeedbackConverter feedbackConverter) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.efecteEmailAddress = efecteEmailAddress;
        this.anonymousFeedbackFromAddress = anonymousFeedbackFromAddress;
        this.anonymousFeedbackReplyToAddress = anonymousFeedbackReplyToAddress;
        this.feedbackRepository = feedbackRepository;
        this.feedbackConverter = feedbackConverter;
    }

    public void insertFeedback(InsertFeedbackRequest request) throws Exception {
        String content = request.content;
        String timestamp = createTimestamp();
        String state = getState(request);
        String faculty = getFaculty(request);
        String userAgent = getUserAgent(request);
        Locale locale = getLocale(request);
        String body = buildMessageBody(content, timestamp, faculty, state, userAgent, locale);
        SimpleMailMessage message = new SimpleMailMessage();
        if ((request.email == null) || (request.email.length() == 0)) {
            message.setFrom(anonymousFeedbackFromAddress);
            message.setReplyTo(anonymousFeedbackReplyToAddress);
        } else {
            message.setFrom(request.email);
        }
        message.setTo(efecteEmailAddress);
        message.setSubject(messageSource.getMessage("subject." + state, null, "Palaute", locale));
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Mail error sending feedback", ex);
            throw new RuntimeException(ex);
        }
    }

    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedDateDesc()
            .stream()
            .map(feedbackConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<FeedbackDto> updateFeedback(Long feedbackId, FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepository.findOne(feedbackId);
        feedback.processed = feedbackDto.processed;
        feedback.comment = feedbackDto.comment;
        return getAllFeedback();
    }

    private String createTimestamp() {
        ZonedDateTime helsinkiNow = ZonedDateTime.now(HELSINKI_ZONE_ID);
        return helsinkiNow.format(TIMESTAMP_FORMATTER);
    }

    private String getState(InsertFeedbackRequest request) {
        return request.metadata.findValue("state").textValue();
    }

    private String getFaculty(InsertFeedbackRequest request) {
        JsonNode facultyNode = request.metadata.findValue("faculty");
        return facultyNode == null ? null : facultyNode.textValue();
    }

    private String getUserAgent(InsertFeedbackRequest request) {
        return request.metadata.findValue("userAgent").textValue();
    }

    private Locale getLocale(InsertFeedbackRequest request) {
        JsonNode langNode = request.metadata.findValue("lang");
        String langCode = langNode == null ? "fi" : langNode.textValue();
        return CODES_TO_LOCALES.get(langCode);
    }

    private String buildMessageBody(String content,
                                    String timestamp,
                                    String faculty,
                                    String state,
                                    String userAgent,
                                    Locale locale) {
        String timestampLine = getTimestampLine(timestamp, locale);
        String stateLine = getStateLine(state, locale);
        String userAgentLine = getUserAgentLine(userAgent, locale);
        if (faculty == null) {
            return String.join("\n", content, "", timestampLine, stateLine, userAgentLine);
        } else {
            String facultyLine = getFacultyLine(faculty, locale);
            return String.join("\n", content, "", timestampLine, facultyLine, stateLine, userAgentLine);
        }
    }

    private String getTimestampLine(String timestamp, Locale locale) {
        return getNameValueLine("timestamp", timestamp, locale);
    }

    private String getFacultyLine(String faculty, Locale locale) {
        return getNameValueLine("faculty", messageSource.getMessage("faculty." + faculty, null, faculty, locale), locale);
    }

    private String getStateLine(String state, Locale locale) {
        return getNameValueLine("state", messageSource.getMessage("state." + state, null, state, locale), locale);
    }

    private String getUserAgentLine(String userAgent, Locale locale) {
        return getNameValueLine("userAgent", userAgent, locale);
    }

    private String getNameValueLine(String name, String value, Locale locale) {
        return messageSource.getMessage("label." + name, null, locale) + ": " + value;
    }
}
