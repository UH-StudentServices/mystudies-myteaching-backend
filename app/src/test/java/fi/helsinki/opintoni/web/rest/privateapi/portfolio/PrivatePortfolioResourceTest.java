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
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.portfolio.DegreeDto;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.dto.portfolio.KeywordDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.portfolio.PortfolioService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.hybridUserSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherWithoutPortfolioSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivatePortfolioResourceTest extends AbstractPortfolioResourceTest {

    private static final String STUDENT_PORTFOLIO_PATH = "/portfolio/en/olli-opiskelija";
    private static final String TEACHER_PORTFOLIO_PATH = "/portfolio/en/olli-opettaja";
    private static final String HYBRID_USER_PORTFOLIO_PATH = "/portfolio/en/hybrid-user";
    private static final String ANOTHER_USERS_PRIVATE_PORTFOLIO_PATH = "/student/en/test-test";
    private static final String STUDENT_EMAIL = "olli.opiskelija@helsinki.fi";

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Test
    public void thatAnyExistingOwnPortfolioIsReturnedWhenQueryingByRoleOnly() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_API_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(jsonPath("$.id").value(isOneOf(2, 5)));
    }

    @Test
    public void thatStudentPortfolioContainsAllLinkedComponents() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_API_PATH + "/en/olli-opiskelija")
            .with(securityContext(studentSecurityContext())))
            .andExpect(jsonPath("$.contactInformation").value(
                both(hasEntry("email", STUDENT_EMAIL)).and(hasEntry("phoneNumber", "+358112223333"))
            ))
            .andExpect(jsonPath("$.degrees").value(Matchers.<List<DegreeDto>>allOf(
                hasSize(1),
                hasItem(
                    both(
                        hasEntry("title", "Luonnontieteiden kandidaatti")).and(
                        hasEntry("description", "Globaalit rakenneoptimointimenetelmät")
                    )
                )
            )))
            .andExpect(jsonPath("$.workExperience").value(Matchers.<List<WorkExperienceDto>>allOf(
                hasSize(1),
                hasItem(
                    both(hasEntry("employer", "HY"))
                        .and(hasEntry("jobTitle", "Harjoittelija"))
                        .and(hasEntry("employerUrl", "http://www.helsinki.fi/"))
                        .and(hasEntry("text", "Lorem ipsum")
                        )
                )
            )))
            .andExpect(jsonPath("$.jobSearch").value(hasEntry("contactEmail", STUDENT_EMAIL)))
            .andExpect(jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    both(hasEntry("title", "Otsikko")).and(hasEntry("text", "Teksti"))
                )
            )))
            .andExpect(jsonPath("$.languageProficiencies").value(Matchers.<List<LanguageProficiencyDto>>allOf(
                hasSize(3),
                hasItem(
                    both(hasEntry("id", 1)).and(hasEntry("language", "en")).and(hasEntry("proficiency", 4))
                ),
                hasItem(
                    both(hasEntry("id", 2)).and(hasEntry("language", "fi")).and(hasEntry("proficiency", 5))
                ),
                hasItem(
                    both(hasEntry("id", 4)).and(hasEntry("language", "hi")).and(hasEntry("proficiency", 2))
                )
            )))
            .andExpect(jsonPath("$.keywords").value(Matchers.<List<KeywordDto>>allOf(
                hasSize(1),
                hasItem(
                    hasEntry("title", "Keyword 1")
                )
            )))
            .andExpect(jsonPath("$.summary", is("Summary")))
            .andExpect(jsonPath("$.favorites").value(Matchers.<List<FavoriteDto>>allOf(
                hasSize(2),
                hasItem(
                    hasEntry("id", 6)
                ),
                hasItem(
                    hasEntry("id", 8)
                )
            )));
    }

    @Test
    public void thatTeacherPortfolioContainsAllLinkedComponents() throws Exception {
        mockMvc.perform(get(TEACHER_PORTFOLIO_API_PATH + "/fi/opettaja")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                hasSize(1),
                hasItem(
                    both(hasEntry("title", "Otsikko 3"))
                        .and(hasEntry("text", "Teksti 3"))
                        .and(hasEntry("portfolioSection", "RESEARCH"))
                )
            )));
    }

    @Test
    public void thatPortfolioIsFoundByPath() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_API_PATH + "/en/olli-opiskelija")
            .with(securityContext(studentSecurityContext()))
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
    public void thatAnotherUsersPrivatePortfolioIsNotFound() throws Exception {
        mockMvc.perform(get(PRIVATE_PORTFOLIO_API_PATH + ANOTHER_USERS_PRIVATE_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext()))
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
            .andExpect(jsonPath("$.url").value(STUDENT_PORTFOLIO_PATH))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatTeacherPortfolioIsCreatedInSessionLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherPortfolio();

        createTeacherPortfolio(teacherSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(TEACHER_PORTFOLIO_PATH))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatTeacherPortfolioIsCreatedWithCorrectContactInfo() throws Exception {
        esbServer.expectEmployeeContactInformationRequest("010540");

        mockMvc.perform(post(TEACHER_PORTFOLIO_API_PATH)
            .with(securityContext(teacherWithoutPortfolioSecurityContext())))
            .andExpect(jsonPath("$.contactInformation.email").value("olli.opettaja@helsinki.fi"))
            .andExpect(jsonPath("$.contactInformation.workNumber").value("54321"))
            .andExpect(jsonPath("$.contactInformation.workMobile").value("12345678"))
            .andExpect(jsonPath("$.contactInformation.title").value("universitetslektor"))
            .andExpect(jsonPath("$.contactInformation.faculty").value("Käyttäytymistieteellinen tiedekunta"))
            .andExpect(jsonPath("$.contactInformation.financialUnit").value("OIKTDK, Faculty of Law"))
            .andExpect(jsonPath("$.contactInformation.workAddress").value("PL 9 (Siltavuorenpenger 1A)"))
            .andExpect(jsonPath("$.contactInformation.workPostcode").value("00014 HELSINGIN YLIOPISTO"));
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
            .andExpect(jsonPath("$.url").value(HYBRID_USER_PORTFOLIO_PATH))
            .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherPortfolio(hybridUserSecurityContext())
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value(HYBRID_USER_PORTFOLIO_PATH))
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

        assertThat(portfolioService.findById(2L).visibility)
            .isEqualTo(PortfolioVisibility.PUBLIC);
    }

    private void deleteExistingStudentPortfolios() {
        portfolioRepository.delete(2L);
        portfolioRepository.delete(5L);
    }

    private void deleteExistingTeacherPortfolio() {
        portfolioRepository.delete(4L);
    }
}
