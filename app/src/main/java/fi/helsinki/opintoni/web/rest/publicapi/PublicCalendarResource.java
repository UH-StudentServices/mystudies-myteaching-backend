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

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.CalendarService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/calendar",
    produces = WebConstants.TEXT_CALENDAR_UTF8)
public class PublicCalendarResource extends AbstractResource {

    private final CalendarService calendarService;

    @Autowired
    public PublicCalendarResource(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @RequestMapping(method = RequestMethod.GET, value="/{feedId}/{language}")
    @Timed
    public ResponseEntity<String> showCalendarFeed(@PathVariable("feedId") String feedId, @PathVariable("language") Language language) {
        return response(calendarService.showCalendarFeed(feedId, language.toLocale()));
    }

    @ExceptionHandler({ConversionFailedException.class})
    public ResponseEntity handleException() {
        return ResponseEntity.notFound().build();
    }
}
