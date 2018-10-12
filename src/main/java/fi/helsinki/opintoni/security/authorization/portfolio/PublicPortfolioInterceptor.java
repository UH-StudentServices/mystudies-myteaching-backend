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

import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PublicPortfolioInterceptor extends HandlerInterceptorAdapter {

    private final PortfolioRequestResolver portfolioRequestResolver;
    private final PublicVisibilityResolver publicVisibilityResolver;
    private final ComponentVisibilityChecker componentVisibilityChecker;

    @Autowired
    public PublicPortfolioInterceptor(PortfolioRequestResolver portfolioRequestResolver,
                                      PublicVisibilityResolver publicVisibilityResolver,
                                      ComponentVisibilityChecker componentVisibilityChecker) {
        this.portfolioRequestResolver = portfolioRequestResolver;
        this.publicVisibilityResolver = publicVisibilityResolver;
        this.componentVisibilityChecker = componentVisibilityChecker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        portfolioRequestResolver.resolve(request).ifPresent(portfolioDto -> {
            verifyPortfolioVisibility(portfolioDto);
            publicVisibilityResolver.resolve(handler).ifPresent(publicVisibility ->
                verifyComponentVisibility(portfolioDto, publicVisibility));
        });

        return true;
    }

    private void verifyPortfolioVisibility(PortfolioDto portfolioDto) {
        if (portfolioDto.visibility != PortfolioVisibility.PUBLIC) {
            throw new NotFoundException("Portfolio not found");
        }
    }

    private void verifyComponentVisibility(PortfolioDto portfolioDto, PublicVisibility publicVisibility) {
        if (!componentVisibilityChecker.isPublic(portfolioDto.id, publicVisibility.value())) {
            throw new ForbiddenException("Forbidden");
        }
    }

}
