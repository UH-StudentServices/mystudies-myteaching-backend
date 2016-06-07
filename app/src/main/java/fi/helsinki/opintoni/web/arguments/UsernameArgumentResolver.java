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

package fi.helsinki.opintoni.web.arguments;

import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static fi.helsinki.opintoni.exception.http.ForbiddenException.forbiddenException;

public class UsernameArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UsernameArgumentResolver(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterAnnotation(Username.class) == null) {
            return false;
        }

        if (parameter.getParameterType() != String.class) {
            throw new ForbiddenException("Username must be of type String");
        }

        return true;
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {

        return securityUtils.getAppUser()
            .map(user -> user.getEduPersonPrincipalName())
            .orElseThrow(forbiddenException("Principal not found or user has no username"));
    }
}