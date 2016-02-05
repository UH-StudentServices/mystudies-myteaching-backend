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

import fi.helsinki.opintoni.config.http.CsvResponse;
import fi.helsinki.opintoni.dto.FeedbackDto;
import fi.helsinki.opintoni.service.FeedbackService;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.ADMIN_API_V1 + "/feedback")
public class FeedbackResource extends AbstractResource {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackResource(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/csv")
    public ResponseEntity<CsvResponse<FeedbackDto>> getFeedbackAsCsv() {
        CsvResponse<FeedbackDto> csvResponse = new CsvResponse<>(FeedbackDto.class);
        csvResponse.filename = "feedback.csv";
        csvResponse.entries = feedbackService.getAllFeedback();
        return response(csvResponse);
    }

    @RequestMapping(
        method = RequestMethod.GET)
    public ResponseEntity<List<FeedbackDto>> getFeedback() {
        return response(feedbackService.getAllFeedback());
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{feedbackId}")
    public ResponseEntity<List<FeedbackDto>> updateFeedback(
        @PathVariable("feedbackId") long feedbackId,
        @RequestBody FeedbackDto feedback) {
        return response(feedbackService.updateFeedback(feedbackId, feedback));
    }
}
