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
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String DOO_PROJECT_EMAIL = "doo-projekti@helsinki.fi";
    private static final String NO_REPLY_EMAIL = "noreply@helsinki.fi";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Map<String, Locale> NAMES_TO_LOCALES = new HashMap<>();
    private static Locale finnishLocale = new Locale("fi", "FI");
    static {
        NAMES_TO_LOCALES.put("fi", finnishLocale);
        NAMES_TO_LOCALES.put("sv", new Locale("sv", "FI"));
        NAMES_TO_LOCALES.put("en", new Locale("en", "FI"));
    }

    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;
    private final FeedbackConverter feedbackConverter;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    @Value("${feedback.efecte.address}")
    private String efecteEmailAddress;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository,
                           ObjectMapper objectMapper,
                           FeedbackConverter feedbackConverter) {
        this.feedbackRepository = feedbackRepository;
        this.objectMapper = objectMapper;
        this.feedbackConverter = feedbackConverter;
    }

    public void insertFeedback(InsertFeedbackRequest request) throws Exception {
        log.error("Inserting feedback, content=\"" + request.content + "\"");
        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).format(TIMESTAMP_FORMATTER);
        String faculty = request.metadata.findValue("faculty").textValue();
        String state = request.metadata.findValue("state").textValue();
        String userAgent = request.metadata.findValue("userAgent").textValue();
        JsonNode langNode = request.metadata.findValue("lang");
        String lang = langNode == null ? "fi" : langNode.textValue();
        Locale locale = NAMES_TO_LOCALES.get(lang);
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(request.content);
        bodyBuilder.append("\n\n");
        bodyBuilder.append(messageSource.getMessage("label.timestamp", null, locale));
        bodyBuilder.append(": ");
        bodyBuilder.append(timestamp);
        bodyBuilder.append("\n");
        bodyBuilder.append(messageSource.getMessage("label.faculty", null, locale));
        bodyBuilder.append(": ");
        bodyBuilder.append(messageSource.getMessage("faculty." + faculty, null, faculty, locale));
        bodyBuilder.append("\n");
        bodyBuilder.append(messageSource.getMessage("label.state", null, locale));
        bodyBuilder.append(": ");
        bodyBuilder.append(messageSource.getMessage("state." + state, null, state, locale));
        bodyBuilder.append("\n");
        bodyBuilder.append(messageSource.getMessage("label.userAgent", null, locale));;
        bodyBuilder.append(": ");
        bodyBuilder.append(userAgent);
        String body = bodyBuilder.toString();
        SimpleMailMessage message = new SimpleMailMessage();
        if ((request.email == null) || (request.email.length() == 0)) {
            message.setFrom(DOO_PROJECT_EMAIL);
            message.setReplyTo(NO_REPLY_EMAIL);
        } else {
            message.setFrom(request.email);
        }
        message.setTo(efecteEmailAddress);
        message.setSubject(messageSource.getMessage("subject." + state, null, "Palaute", locale));
        message.setText(body);
        try {
            log.error("Sending mail");
            mailSender.send(message);
            log.error("Mail sent OK");
        } catch (MailException ex) {
            // TODO: Check whether we can report this error e.g. 500 status
            log.error("Mail error sending feedback", ex);
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error sending message", ex);
            throw ex;
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
}
