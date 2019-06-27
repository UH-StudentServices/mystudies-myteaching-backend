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

package fi.helsinki.opintoni.security;

import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FederatedAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    private static final String ERROR_PATH = "/error/maintenance";

    @Autowired
    private UriBuilder uriBuilder;

    @Value("${teacherAppUrl}")
    String teacherAppUrl;

    @Value("${studentAppUrl}")
    String studentAppUrl;

    @Override
    protected void handleAuthSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final boolean isTeacher = request.getRequestURI().endsWith(Constants.TEACHER_PATH_END);

        if (request.getSession() != null) {
            final String redirectTarget = (String) request.getSession().getAttribute(Constants.ATTR_NAME_REMEMBER_TARGET);
            if (redirectTarget != null) {
                request.getSession().removeAttribute(Constants.ATTR_NAME_REMEMBER_TARGET);
                if (redirectTarget.equals(Constants.LOGIN_PROFILE_QUERY_STRING)) {
                    response.sendRedirect(uriBuilder.getProfileBaseUrl(isTeacher ? ProfileRole.TEACHER : ProfileRole.STUDENT));
                    return;
                }
            }
        }

        if (isTeacher) {
            response.sendRedirect(teacherAppUrl);
        } else {
            response.sendRedirect(studentAppUrl);
        }
    }

    @Override
    protected void handleAuthFailure(HttpServletResponse response) throws IOException {
        response.sendRedirect(ERROR_PATH);
    }
}
