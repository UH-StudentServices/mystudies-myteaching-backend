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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.Feedback;
import fi.helsinki.opintoni.dto.FeedbackDto;
import fi.helsinki.opintoni.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackConverter {

    private final TimeService timeService;

    @Autowired
    public FeedbackConverter(TimeService timeService) {
        this.timeService = timeService;
    }

    public FeedbackDto toDto(Feedback feedback) {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.content = feedback.content;
        feedbackDto.metadata = feedback.metadata;
        feedbackDto.created = timeService.toHelsinkiTimeFromUTC(feedback.getCreatedDate());
        feedbackDto.email = feedback.email;
        feedbackDto.processed = feedback.processed;
        return feedbackDto;
    }
}
