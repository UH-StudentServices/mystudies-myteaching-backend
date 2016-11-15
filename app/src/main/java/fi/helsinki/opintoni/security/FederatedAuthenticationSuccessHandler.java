package fi.helsinki.opintoni.security;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FederatedAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    private static final String ERROR_PATH = "/error/maintenance";

    @Override
    protected void handleAuthSuccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    @Override
    protected void handleAuthFailure(HttpServletResponse response) throws IOException {
        response.sendRedirect(ERROR_PATH);
    }
}
