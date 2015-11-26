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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.jobSearch;

import fi.helsinki.opintoni.domain.portfolio.JobSearch;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.portfolio.JobSearchDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.portfolio.JobSearchService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/jobsearch",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PrivateJobSearchResource extends AbstractResource {

    private final PermissionChecker permissionChecker;
    private final JobSearchService jobSearchService;

    @Autowired
    public PrivateJobSearchResource(PermissionChecker permissionChecker, JobSearchService jobSearchService) {
        this.permissionChecker = permissionChecker;
        this.jobSearchService = jobSearchService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<JobSearchDto> findByPortfolioId(@UserId Long userId, @PathVariable Long portfolioId) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(jobSearchService.findByPortfolioId(portfolioId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<JobSearchDto> insert(@UserId Long userId,
                                               @PathVariable Long portfolioId,
                                               @RequestBody JobSearchDto jobSearchDto) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(jobSearchService.insert(portfolioId, jobSearchDto));
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{jobSearchId}")
    public ResponseEntity<Void> insert(@UserId Long userId,
                                       @PathVariable Long jobSearchId) {
        permissionChecker.verifyPermission(userId, jobSearchId, JobSearch.class);
        jobSearchService.delete(jobSearchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
