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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.languageproficiency;

import fi.helsinki.opintoni.dto.portfolio.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/languageproficiencies",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateLanguageProficiencyResource extends AbstractResource {
    private final LanguageProficiencyService languageProficiencyService;

    @Autowired
    public PrivateLanguageProficiencyResource(LanguageProficiencyService languageProficiencyService) {
        this.languageProficiencyService = languageProficiencyService;
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<List<LanguageProficiencyDto>> update(@UserId Long userId,
                                                               @PathVariable Long portfolioId,
                                                               @RequestBody LanguageProficienciesChangeDescriptorDto changeDescriptor) {
        languageProficiencyService.updateLanguageProficiencies(changeDescriptor, portfolioId, userId);
        return response(languageProficiencyService.findByPortfolioId(portfolioId));
    }
}
