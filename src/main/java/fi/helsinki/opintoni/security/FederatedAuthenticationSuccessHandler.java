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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FederatedAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    private static final String ERROR_PATH = "/error/maintenance";
    private static final String TEACHER_PATH_END = "teacher";

    @Value("${teacherAppUrl}")
    String teacherAppUrl;

    @Value("${studentAppUrl}")
    String studentAppUrl;

    @Override
    protected void handleAuthSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith(TEACHER_PATH_END)) {
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
