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

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.dto.ObarJWTTokenDto;
import fi.helsinki.opintoni.integration.obar.ObarJWTService;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static fi.helsinki.opintoni.integration.obar.Constants.LANG_COOKIE_NAME;

@RestController
@RequestMapping(value = RestConstants.PUBLIC_API_V1, produces = WebConstants.APPLICATION_JSON_UTF8)
@ConditionalOnProperty(prefix = "obar", name = "baseUrl")
public class PublicObarJWTResource extends AbstractResource {
    public static final String PARAM_APP_NAME = "app";

    @Autowired
    private AppConfiguration appConfiguration;

    protected final ObarJWTService obarJWTService;
    protected final SecurityUtils securityUtils;

    @Autowired
    public PublicObarJWTResource(ObarJWTService obarJWTService, SecurityUtils securityUtils) {
        this.obarJWTService = obarJWTService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/obar-jwt-token")
    public ResponseEntity<ObarJWTTokenDto> getObarJWTToken(
            @CookieValue(name = LANG_COOKIE_NAME, required = false) String obarLang,
            @Nullable @RequestParam(name = PARAM_APP_NAME) String app) {
        AppUser user = securityUtils.getAppUser().orElse(null);
        String loginUrl = appConfiguration.get("loginUrlStudent");
        if (app != null && app.equals("profile")) {
            loginUrl += Constants.LOGIN_PROFILE_SUFFIX;
        }
        return response(new ObarJWTTokenDto(obarJWTService.generateToken(user, obarLang, loginUrl)));
    }
}
