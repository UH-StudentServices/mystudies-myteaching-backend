package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.ResultActions;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractPortfolioResourceTest extends SpringTest {

    private static final String EMPLOYEE_NUMBER = "010540";

    protected static final String PORTFOLIO_API_URL = "/api/private/v1/portfolio";
    protected static final String STUDENT_PORTFOLIO_API_URL = PORTFOLIO_API_URL + "/student";
    protected static final String TEACHER_PORTFOLIO_API_URL = PORTFOLIO_API_URL + "/teacher";

    protected ResultActions createPortfolio(SecurityContext securityContext, String apiUrl) throws Exception {
        return mockMvc.perform(post(apiUrl)
            .with(securityContext(securityContext))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions createStudentPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, STUDENT_PORTFOLIO_API_URL);
    }

    protected ResultActions createTeacherPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, TEACHER_PORTFOLIO_API_URL);
    }

    protected void expectEmployeeContactInformationRequestToESB() {
        esbServer.expectEmployeeContactInformationRequest(EMPLOYEE_NUMBER);
    }

}
