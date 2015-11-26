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

package fi.helsinki.opintoni.web.rest.restrictedapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.security.authorization.portfolio.PublicVisibility;
import fi.helsinki.opintoni.service.portfolio.WorkExperienceService;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.helsinki.opintoni.web.WebConstants.APPLICATION_JSON_UTF8;
import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.RESTRICTED_API_V1;

@RestController
@RequestMapping(
    value = RESTRICTED_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/workexperience",
    produces = APPLICATION_JSON_UTF8)
@PublicVisibility(PortfolioComponent.WORK_EXPERIENCE)
public class RestrictedWorkExperienceResource extends AbstractResource {

    private final WorkExperienceService workExperienceService;

    @Autowired
    public RestrictedWorkExperienceResource(WorkExperienceService workExperienceService) {
        this.workExperienceService = workExperienceService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<WorkExperienceDto>> findByPortfolioId(@PathVariable Long portfolioId) {
        return response(workExperienceService.findByPortfolioId(portfolioId));
    }
}
