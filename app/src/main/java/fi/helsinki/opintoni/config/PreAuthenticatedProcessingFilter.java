package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.AuthFailureHandler;
import fi.helsinki.opintoni.security.BaseAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.LocalAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.util.AuditLogger;
import org.springframework.core.env.Environment;
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
        //TODO: Extract user data from request headers when headers are sent by proxy
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("opiskelija@helsinki.fi")
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.MEMBER, SAMLEduPersonAffiliation.STUDENT))
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .email("opiskelija@mail.helsinki.fi")
            .commonName("Olli Opiskelija")
            .studentNumber("010189791")
            .oodiPersonId("1001")
            .build();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, authResult);

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);

        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
