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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.sample;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.portfolio.SampleDto;
import fi.helsinki.opintoni.service.portfolio.SampleService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PORTFOLIO + "/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/samples",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateSampleResource extends AbstractResource {

    private final SampleService sampleService;

    @Autowired
    public PrivateSampleResource(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<SampleDto>> update(@UserId Long userId,
                                                  @PathVariable Long portfolioId,
                                                  @RequestBody List<UpdateSample> updateSamples) {
        return response(sampleService.updateSamples(portfolioId, updateSamples));
    }
}
