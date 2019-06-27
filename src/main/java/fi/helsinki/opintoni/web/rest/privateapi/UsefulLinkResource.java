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
import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.dto.OrderUsefulLinksDto;
import fi.helsinki.opintoni.dto.SearchPageTitleDto;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.usefullink.UsefulLinkService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/usefullinks",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class UsefulLinkResource extends AbstractResource {
    private final UsefulLinkService usefulLinkService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public UsefulLinkResource(UsefulLinkService usefulLinkService, PermissionChecker permissionChecker) {
        this.usefulLinkService = usefulLinkService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<UsefulLinkDto>> getAll(@UserId Long userId, Locale locale) {
        return response(usefulLinkService.findByUserId(userId, locale));
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<UsefulLinkDto> insert(@UserId Long userId,
                                                @Valid @RequestBody UsefulLinkDto usefulLinkDto, Locale locale) {
        return response(usefulLinkService.insert(userId, usefulLinkDto, locale));
    }

    @RequestMapping(value = "/{usefulLinkId}", method = RequestMethod.PUT)
    @Timed
    public ResponseEntity<UsefulLinkDto> update(@UserId Long userId,
                                                @PathVariable("usefulLinkId") Long usefulLinkId,
                                                @Valid @RequestBody UsefulLinkDto usefulLinkDto,
                                                Locale locale) {
        permissionChecker.verifyPermission(userId, usefulLinkId, UsefulLink.class);
        return response(usefulLinkService.update(usefulLinkId, usefulLinkDto, locale));
    }

    @RequestMapping(
        value = "/order",
        method = RequestMethod.POST,
        produces = WebConstants.APPLICATION_JSON_UTF8
    )
    @Timed
    public ResponseEntity<List<UsefulLinkDto>> orderUsefulLinks(@UserId Long userId,
                                                                @RequestBody OrderUsefulLinksDto orderUsefulLinksDto,
                                                                Locale locale) {
        permissionChecker.verifyPermission(userId, orderUsefulLinksDto.usefulLinkIds, UsefulLink.class);
        return response(usefulLinkService.updateOrder(userId, orderUsefulLinksDto, locale));
    }

    @RequestMapping(value = "/{usefulLinkId}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<List<UsefulLinkDto>> delete(@UserId Long userId,
                                                      @PathVariable("usefulLinkId") Long usefulLinkId,
                                                      Locale locale) {
        permissionChecker.verifyPermission(userId, usefulLinkId, UsefulLink.class);
        return response(() -> {
            usefulLinkService.delete(usefulLinkId);
            return usefulLinkService.findByUserId(userId, locale);
        });
    }

    @RequestMapping(value = "/searchpagetitle", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<SearchPageTitleDto> searchPageTitle(@Valid @RequestBody SearchPageTitleDto
                                                                  searchPageTitleDto) {
        return response(usefulLinkService.searchPageTitle(searchPageTitleDto));
    }
}
