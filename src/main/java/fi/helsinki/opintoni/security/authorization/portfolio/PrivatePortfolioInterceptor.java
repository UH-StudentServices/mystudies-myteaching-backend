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

package fi.helsinki.opintoni.security.authorization.portfolio;

import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class PrivatePortfolioInterceptor extends HandlerInterceptorAdapter {

    private final UserService userService;
    private final PermissionChecker permissionChecker;
    private final PortfolioRequestResolver portfolioRequestResolver;
    private final SecurityUtils securityUtils;

    @Autowired
    public PrivatePortfolioInterceptor(UserService userService,
                                       PermissionChecker permissionChecker,
                                       PortfolioRequestResolver portfolioRequestResolver,
                                       SecurityUtils securityUtils) {
        this.userService = userService;
        this.permissionChecker = permissionChecker;
        this.portfolioRequestResolver = portfolioRequestResolver;
        this.securityUtils = securityUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        portfolioRequestResolver.resolve(request).ifPresent(portfolioDto -> {
            if (!permissionChecker.hasPermission(getUserId(), portfolioDto.id, Portfolio.class)) {
                throw new NotFoundException("Portfolio not found");
            }
        });

        return true;
    }

    private Long getUserId() {
        return securityUtils.getAppUser()
            .flatMap(this::findUser)
            .map(u -> u.id)
            .orElse(null);
    }

    private Optional<User> findUser(AppUser appUser) {
        return userService.findFirstByEduPersonPrincipalName(appUser.getEduPersonPrincipalName());
    }
}
