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

import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
    This API is used by other services. Do not delete!
 */
@RestController
@RequestMapping(RestConstants.PUBLIC_API_V1)
public class SystemResource {

    @RequestMapping(value = "/health-check",
        method = RequestMethod.GET,
        produces = WebConstants.APPLICATION_JSON_UTF8)
    public ResponseEntity<HealthStatus> getHealthStatus() {
        return new ResponseEntity<>(new HealthStatus(), HttpStatus.OK);
    }

    public static class HealthStatus {

        public String getStatus() {
            return "ok";
        }

    }
}