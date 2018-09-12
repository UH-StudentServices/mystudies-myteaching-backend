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

package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.AuthFailureHandler;
import fi.helsinki.opintoni.security.BaseAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.LocalAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.util.AuditLogger;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class PreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final BaseAuthenticationSuccessHandler successHandler;
    private final AuthFailureHandler failureHandler;

    public PreAuthenticatedProcessingFilter(UserService userService, Environment environment, AuditLogger auditLogger) {
        super();

        LocalAuthenticationSuccessHandler successHandler = new LocalAuthenticationSuccessHandler();
        successHandler.initialize(userService, environment, auditLogger);

        this.successHandler = successHandler;
        this.failureHandler = new AuthFailureHandler();
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String eduPersonPrincipalName = request.getHeader("HY-USER-eduPersonPrincipalName");
        String email = request.getHeader("HY-USER-mail");
        String commonName = request.getHeader("HY-USER-commonName");
        String studentNumber = request.getHeader("HY-USER-studentNumber");
        String oodiPersonId = request.getHeader("HY-USER-oodiUid");

        try {
            return new AppUser.AppUserBuilder()
                .eduPersonPrincipalName(eduPersonPrincipalName)
                .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.MEMBER, SAMLEduPersonAffiliation.STUDENT))
                .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
                .email(email)
                .commonName(commonName)
                .studentNumber(studentNumber)
                .oodiPersonId(oodiPersonId)
                .build();
        } catch (BadCredentialsException e) {
            return null;
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
        throws IOException, ServletException {
        super.successfulAuthentication(request, response, authResult);

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);

        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
