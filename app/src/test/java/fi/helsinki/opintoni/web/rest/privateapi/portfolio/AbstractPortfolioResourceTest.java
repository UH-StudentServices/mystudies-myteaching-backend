package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractPortfolioResourceTest extends SpringTest {

    protected static final String PRIVATE_PORTFOLIO_API_PATH = RestConstants.PRIVATE_API_V1 + "/portfolio";
    protected static final String STUDENT_PORTFOLIO_API_PATH = PRIVATE_PORTFOLIO_API_PATH + "/student";
    protected static final String TEACHER_PORTFOLIO_API_PATH = PRIVATE_PORTFOLIO_API_PATH + "/teacher";
    protected static final String SESSION_LANG = Language.EN.getCode();

    private static final String EMPLOYEE_NUMBER = "010540";
    private static final String ENCODED_DOUBLE_QUOTE = "%22";

    protected ResultActions createPortfolio(SecurityContext securityContext, String apiUrl) throws Exception {
        return mockMvc.perform(post(apiUrl)
            .cookie(localeCookie(Language.EN))
            .with(securityContext(securityContext))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions createStudentPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, STUDENT_PORTFOLIO_API_PATH);
    }

    protected ResultActions createTeacherPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, TEACHER_PORTFOLIO_API_PATH);
    }

    protected ResultActions createStudentPortfolio(SecurityContext securityContext, Language lang) throws Exception {
        return createPortfolio(securityContext, String.join("/", STUDENT_PORTFOLIO_API_PATH, lang.getCode()));
    }

    protected ResultActions createTeacherPortfolio(SecurityContext securityContext, Language lang) throws Exception {
        return createPortfolio(securityContext, String.join("/", TEACHER_PORTFOLIO_API_PATH, lang.getCode()));
    }

    protected void expectEmployeeContactInformationRequestToESB() {
        esbServer.expectEmployeeContactInformationRequest(EMPLOYEE_NUMBER);
    }

    private static Cookie localeCookie(Language lang) {
        return new Cookie(Constants.NG_TRANSLATE_LANG_KEY, ENCODED_DOUBLE_QUOTE + lang.getCode() + ENCODED_DOUBLE_QUOTE);
    }
}
