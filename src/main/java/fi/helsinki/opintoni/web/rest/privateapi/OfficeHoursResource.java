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

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.dto.TeachingLanguageDto;
import fi.helsinki.opintoni.service.OfficeHoursService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/officehours",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class OfficeHoursResource extends AbstractResource {

    private final OfficeHoursService officeHoursService;

    @Autowired
    public OfficeHoursResource(OfficeHoursService officeHoursService) {
        this.officeHoursService = officeHoursService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<OfficeHoursDto>> getOwnOfficeHours(@UserId Long userId) {
        return response(officeHoursService.getByUserId(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<OfficeHoursDto>> saveOwnOfficeHours(
        @UserId Long userId,
        @Valid @RequestBody InsertOfficeHoursRequest request) {
        return response(officeHoursService.update(userId, request.officeHours));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity deleteOwnOfficeHours(@UserId Long userId) {
        officeHoursService.delete(userId);
        return noContentResponse();
    }

    @GetMapping(value = "/teachinglanguages")
    public ResponseEntity<List<TeachingLanguageDto>> getTeachingLanguages() {
        return response(officeHoursService.getTeachingLanguages());
    }
}
