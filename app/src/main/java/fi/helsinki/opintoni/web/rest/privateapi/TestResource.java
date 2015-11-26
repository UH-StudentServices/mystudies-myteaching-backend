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
import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Profile({"local-dev", "dev", "qa"})
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/test",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class TestResource extends AbstractResource {

    @RequestMapping(value = "/internalservererror", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<Map<String, String>> return500Error() {
        return new ResponseEntity<>(ImmutableMap.of("message", "error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/whatismyip", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<String> whatIsMyIp(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(httpServletRequest.getRemoteAddr(), HttpStatus.OK);
    }

}
