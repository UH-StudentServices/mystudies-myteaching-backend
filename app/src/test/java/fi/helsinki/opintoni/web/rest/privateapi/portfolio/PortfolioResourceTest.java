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


import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PortfolioResourceTest extends AbstractPortfolioResourceTest {

    private static final String EXPECTED_STUDENT_PORTFOLIO_URL = "/portfolio/en/olli-opiskelija";
    private static final String EXPECTED_TEACHER_PORTFOLIO_URL = "/portfolio/en/olli-opettaja";
    private static final String EXPECTED_HYBRID_USER_STUDENT_PORTFOLIO_URL = "/portfolio/en/hybrid-user";
    private static final String EXPECTED_HYBRID_USER_TEACHER_PORTFOLIO_URL = "/portfolio/en/hybrid-user";

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    public void thatNotFoundIsReturned() throws Exception {
        mockMvc.perform(get(PRIVATE_PORTFOLIO_API_PATH).with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatStudentPortfolioIsCreatedInSessionLang() throws Exception {
        deleteExistingStudentPortfolios();

        createStudentPortfolio(studentSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(EXPECTED_STUDENT_PORTFOLIO_URL))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatTeacherPortfolioIsCreatedInSessionLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherPortfolio();

        createTeacherPortfolio(teacherSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(EXPECTED_TEACHER_PORTFOLIO_URL))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatStudentCanCreateMultiplePortfoliosInDifferentLangs() throws Exception {
        deleteExistingStudentPortfolios();

        createStudentPortfolio(studentSecurityContext())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createStudentPortfolio(studentSecurityContext(), Language.FI)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatStudentCannotCreateMultiplePortfoliosInSameLang() throws Exception {
        deleteExistingStudentPortfolios();

        createStudentPortfolio(studentSecurityContext())
            .andExpect(status().isOk());

        createStudentPortfolio(studentSecurityContext())
            .andExpect(status().isInternalServerError());

        createStudentPortfolio(studentSecurityContext(), Language.EN)
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatTeacherCanCreateMultiplePortfoliosInDifferentLangs() throws Exception {
        expectEmployeeContactInformationRequestToESB();
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherPortfolio();

        createTeacherPortfolio(teacherSecurityContext())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherPortfolio(teacherSecurityContext(), Language.FI)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatTeacherCannotCreateMultiplePortfoliosInSameLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherPortfolio();

        createTeacherPortfolio(teacherSecurityContext())
            .andExpect(status().isOk());

        createTeacherPortfolio(teacherSecurityContext())
            .andExpect(status().isInternalServerError());

        createTeacherPortfolio(teacherSecurityContext(), Language.EN)
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatHybridUserCanCreateStudentAndTeacherPortfoliosInSessionLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        createStudentPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(EXPECTED_HYBRID_USER_STUDENT_PORTFOLIO_URL))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(EXPECTED_HYBRID_USER_TEACHER_PORTFOLIO_URL))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatHybridUserCanCreateMultipleStudentPortfoliosInDifferentLangs() throws Exception {
        createStudentPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createStudentPortfolio(hybridUserSecurityContext(), Language.FI)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatHybridUserCannotCreateMultipleStudentPortfoliosInSameLang() throws Exception {
        createStudentPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk());

        createStudentPortfolio(hybridUserSecurityContext())
            .andExpect(status().isInternalServerError());

        createStudentPortfolio(hybridUserSecurityContext(), Language.EN)
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatHybridUserCanCreateMultipleTeacherPortfoliosInDifferentLangs() throws Exception {
        expectEmployeeContactInformationRequestToESB();
        expectEmployeeContactInformationRequestToESB();

        createTeacherPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherPortfolio(hybridUserSecurityContext(), Language.FI)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatHybridUserCannotCreateMultipleTeacherPortfoliosInSameLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        createTeacherPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk());

        createTeacherPortfolio(hybridUserSecurityContext())
            .andExpect(status().isInternalServerError());

        createTeacherPortfolio(hybridUserSecurityContext(), Language.EN)
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatPortfolioIsFoundByPath() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_API_URL + "/en/olli-opiskelija").with(securityContext
            (studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(
                "https://opi-1.student.helsinki.fi/api/public/v1/images/backgrounds/Profile_3.jpg"
            ))
            .andExpect(jsonPath("$.componentVisibilities").isArray())
            .andExpect(jsonPath("$.componentVisibilities[0].component").value("WORK_EXPERIENCE"))
            .andExpect(jsonPath("$.componentVisibilities[0].visibility").value("PUBLIC"));
    }

    @Test
    public void thatPortfolioIsUpdated() throws Exception {
        String updatedOwnerName = "Olli Opiskelija updated";
        String updatedIntro = "Introtext updated";
        PortfolioVisibility updatedVisibility = PortfolioVisibility.PUBLIC;

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.ownerName = updatedOwnerName;
        portfolioDto.intro = updatedIntro;
        portfolioDto.visibility = updatedVisibility;

        mockMvc.perform(put(PRIVATE_PORTFOLIO_API_PATH + "/2").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(portfolioDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.ownerName").value(updatedOwnerName))
            .andExpect(jsonPath("$.intro").value(updatedIntro))
            .andExpect(jsonPath("$.visibility").value(updatedVisibility.name()));
    }

    private void deleteExistingStudentPortfolios() {
        portfolioRepository.delete(2L);
        portfolioRepository.delete(5L);
    }

    private void deleteExistingTeacherPortfolio() {
        portfolioRepository.delete(4L);
    }
}
