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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.authorization.StudentRoleRequired;
import fi.helsinki.opintoni.security.authorization.TeacherRoleRequired;
import fi.helsinki.opintoni.service.converter.PortfolioConverter;
import fi.helsinki.opintoni.service.portfolio.PortfolioService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/portfolio",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PrivatePortfolioResource extends AbstractResource {
    private final PortfolioService portfolioService;

    @Autowired
    public PrivatePortfolioResource(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @RequestMapping(value = "/{portfolioRole}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<PortfolioDto> get(@PathVariable("portfolioRole") String portfolioRole, @UserId Long userId) {
        return response(portfolioService.get(
            userId,
            PortfolioRole.fromValue(portfolioRole)));
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @Timed
    @StudentRoleRequired
    public ResponseEntity<PortfolioDto> insertStudentPortfolio(@UserId Long userId,
                                               @AuthenticationPrincipal AppUser appUser) {
        return response(portfolioService.insert(
            userId,
            appUser.getCommonName(),
            PortfolioRole.STUDENT));
    }

    @RequestMapping(value = "/teacher", method = RequestMethod.POST)
    @Timed
    @TeacherRoleRequired
    public ResponseEntity<PortfolioDto> insertTeacherPortfolio(@UserId Long userId,
                                                               @AuthenticationPrincipal AppUser appUser) {
        return response(portfolioService.insert(
            userId,
            appUser.getCommonName(),
            PortfolioRole.TEACHER));
    }

    @RequestMapping(value = "/{portfolioRole}/{path:.*}", method = RequestMethod.GET)
    public ResponseEntity<PortfolioDto> findByPath(
        @PathVariable("portfolioRole") String portfolioRole,
        @PathVariable("path") String path) {
        PortfolioDto portfolioDto = portfolioService.findByPath(path, PortfolioRole.fromValue(portfolioRole),
            PortfolioConverter.ComponentFetchStrategy.PUBLIC);
        return response(portfolioDto);
    }

    @RequestMapping(value = "/{portfolioId}", method = RequestMethod.PUT)
    public ResponseEntity<PortfolioDto> update(
        @PathVariable("portfolioId") Long portfolioId,
        @Valid @RequestBody PortfolioDto portfolioDto) {
        return response(portfolioService.update(portfolioId, portfolioDto));
    }
}
