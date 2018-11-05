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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.ResultActions;

import static fi.helsinki.opintoni.localization.Language.EN;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractPortfolioResourceTest extends SpringTest {

    protected static final String PRIVATE_PORTFOLIO_API_PATH = RestConstants.PRIVATE_API_V1 + "/profile";
    protected static final String STUDENT_PORTFOLIO_API_PATH = PRIVATE_PORTFOLIO_API_PATH + "/student";
    protected static final String TEACHER_PORTFOLIO_API_PATH = PRIVATE_PORTFOLIO_API_PATH + "/teacher";
    protected static final String SESSION_LANG = EN.getCode();

    private static final String EMPLOYEE_NUMBER = "010540";

    protected ResultActions createPortfolio(SecurityContext securityContext, String apiUrl) throws Exception {
        return mockMvc.perform(post(apiUrl)
            .cookie(langCookie(EN))
            .with(securityContext(securityContext))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions createStudentPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, STUDENT_PORTFOLIO_API_PATH);
    }

    protected ResultActions createStudentPortfolio(SecurityContext securityContext, Language lang) throws Exception {
        return createPortfolio(securityContext, String.join("/", STUDENT_PORTFOLIO_API_PATH, lang.getCode()));
    }

    protected ResultActions createTeacherPortfolio(SecurityContext securityContext) throws Exception {
        return createPortfolio(securityContext, TEACHER_PORTFOLIO_API_PATH);
    }

    protected ResultActions createTeacherPortfolio(SecurityContext securityContext, Language lang) throws Exception {
        return createPortfolio(securityContext, String.join("/", TEACHER_PORTFOLIO_API_PATH, lang.getCode()));
    }

    protected void expectEmployeeContactInformationRequestToESB() {
        esbServer.expectEmployeeContactInformationRequest(EMPLOYEE_NUMBER);
    }
}
