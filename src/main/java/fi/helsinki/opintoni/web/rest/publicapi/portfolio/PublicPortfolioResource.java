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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.converter.PortfolioConverter;
import fi.helsinki.opintoni.service.portfolio.PortfolioService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1_PORTFOLIO,
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PublicPortfolioResource extends AbstractResource {

    private final PortfolioService portfolioService;

    @Autowired
    public PublicPortfolioResource(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @RequestMapping(value = "/{portfolioRole}/{lang}/{path:.*}", method = RequestMethod.GET)
    public ResponseEntity<PortfolioDto> getByPath(
        @PathVariable("portfolioRole") String portfolioRole,
        @PathVariable("lang") String portfolioLang,
        @PathVariable("path") String path) {
        return response(portfolioService.findByPathAndLangAndRole(path,
            Language.fromCode(portfolioLang),
            PortfolioRole.fromValue(portfolioRole),
            PortfolioConverter.ComponentFetchStrategy.PUBLIC));
    }
}
