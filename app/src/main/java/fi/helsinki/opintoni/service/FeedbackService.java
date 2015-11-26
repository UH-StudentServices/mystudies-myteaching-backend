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

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.domain.Feedback;
import fi.helsinki.opintoni.dto.FeedbackDto;
import fi.helsinki.opintoni.repository.FeedbackRepository;
import fi.helsinki.opintoni.service.converter.FeedbackConverter;
import fi.helsinki.opintoni.web.rest.privateapi.InsertFeedbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;
    private final FeedbackConverter feedbackConverter;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository,
                           ObjectMapper objectMapper,
                           FeedbackConverter feedbackConverter) {
        this.feedbackRepository = feedbackRepository;
        this.objectMapper = objectMapper;
        this.feedbackConverter = feedbackConverter;
    }

    public void insertFeedback(InsertFeedbackRequest request) throws Exception {
        Feedback feedback = new Feedback();
        feedback.content = request.content;
        feedback.email = request.email;
        feedback.metadata = objectMapper.writeValueAsString(request.metadata);
        feedbackRepository.save(feedback);
    }

    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll()
            .stream()
            .map(feedbackConverter::toDto)
            .collect(Collectors.toList());
    }
}
