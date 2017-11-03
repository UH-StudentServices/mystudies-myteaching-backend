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

package fi.helsinki.opintoni.web.rest.publicapi.v2;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.v2.PublicOfficeHoursDto;
import fi.helsinki.opintoni.service.v2.OfficeHoursServiceV2;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V2 + "/officehours",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PublicOfficeHoursResourceV2 extends AbstractResource {

    private final OfficeHoursServiceV2 officeHoursService;

    @Autowired
    public PublicOfficeHoursResourceV2(OfficeHoursServiceV2 officeHoursService) {
        this.officeHoursService = officeHoursService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<PublicOfficeHoursDto>> getAllOfficeHours() {
        return response(officeHoursService.getAll());
    }
}
