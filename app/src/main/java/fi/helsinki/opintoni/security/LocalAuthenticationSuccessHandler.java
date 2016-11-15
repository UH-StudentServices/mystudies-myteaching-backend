package fi.helsinki.opintoni.security;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LocalAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    @Override
    protected void handleAuthSuccess(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void handleAuthFailure(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
