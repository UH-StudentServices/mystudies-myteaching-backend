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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.dto.profile.*;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PrivateProfileResourceTest extends AbstractProfileResourceTest {

    private static final String STUDENT_PROFILE_PATH = "/profile/en/olli-opiskelija";
    private static final String TEACHER_PROFILE_PATH = "/profile/en/olli-opettaja";
    private static final String HYBRID_USER_PROFILE_PATH = "/profile/en/hybrid-user";
    private static final String STUDENT_EMAIL = "olli.opiskelija@helsinki.fi";

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    public void thatAnyExistingOwnProfileIsReturnedWhenQueryingByRoleOnly() throws Exception {
        mockMvc.perform(get(STUDENT_PROFILE_API_PATH)
                .with(securityContext(studentSecurityContext())))
                .andExpect(jsonPath("$.id").value(isOneOf(2, 5)));
    }

    @Test
    public void thatStudentProfileContainsAllLinkedComponents() throws Exception {
        mockMvc.perform(get(STUDENT_PROFILE_API_PATH + "/en/olli-opiskelija")
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
                        hasSize(3),
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
                        hasSize(2),
                        hasItem(
                                both(hasEntry("title", "Otsikko")).and(hasEntry("text", "Teksti"))
                        ),
                        hasItem(
                                both(hasEntry("title", "Otsikko 2")).and(hasEntry("text", "Teksti 2"))
                        )
                )))
                .andExpect(jsonPath("$.keywords").value(Matchers.<List<KeywordDto>>allOf(
                        hasSize(1),
                        hasItem(
                                hasEntry("title", "Keyword 1")
                        )
                )))
                .andExpect(jsonPath("$.summary", is("Summary")))
                .andExpect(jsonPath("$.languageProficiencies").value(Matchers.<List<LanguageProficiencyDto>>allOf(
                        hasSize(3),
                        hasItem(
                                both(hasEntry("id", 1))
                                        .and(hasEntry("languageName", "English"))
                                        .and(hasEntry("proficiency", "Full professional"))
                        ),
                        hasItem(
                                both(hasEntry("id", 2))
                                        .and(hasEntry("languageName", "Finnish"))
                                        .and(hasEntry("proficiency", "Native"))
                        ),
                        hasItem(
                                both(hasEntry("id", 4))
                                        .and(hasEntry("languageName", "Hindi"))
                                        .and(hasEntry("proficiency", "Elementary"))
                        )
                )));
    }

    @Test
    public void thatTeacherProfileContainsAllLinkedComponents() throws Exception {
        mockMvc.perform(get(TEACHER_PROFILE_API_PATH + "/fi/opettaja")
                .with(securityContext(teacherSecurityContext())))
                .andExpect(jsonPath("$.freeTextContent").value(Matchers.<List<FreeTextContentDto>>allOf(
                        hasSize(2),
                        hasItem(
                                both(hasEntry("title", "Tekstikenttä osion sisällä"))
                                        .and(hasEntry("text", "bla bla bla"))
                                        .and(hasEntry("profileSection", "RESEARCH"))
                        ),
                        hasItem(
                                both(hasEntry("title", "Globaali tekstikenttä"))
                                        .and(hasEntry("text", "bla bla bla"))
                        )
                )));
    }

    @Test
    public void thatProfileIsFoundByPath() throws Exception {
        mockMvc.perform(get(STUDENT_PROFILE_API_PATH + "/en/olli-opiskelija")
                .with(securityContext(studentSecurityContext()))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backgroundUri").value(
                        "https://dev.student.helsinki.fi/api/public/v1/images/backgrounds/Profile_3.jpg"
                ))
                .andExpect(jsonPath("$.componentVisibilities").isArray())
                .andExpect(jsonPath("$.componentVisibilities").value(
                    Matchers.hasItem(
                        both(hasEntry("component", ProfileComponent.WORK_EXPERIENCE.toString()))
                            .and(hasEntry("visibility", ProfileVisibility.PUBLIC.toString())))))
                .andExpect(jsonPath("$.componentOrders").value(Matchers.<List<ComponentOrderDto>>allOf(
                        hasSize(3),
                        hasItem(
                                both(hasEntry("component", ProfileComponent.STUDIES.toString()))
                                        .and(hasEntry("orderValue", 1))
                        ),
                        hasItem(
                                both(hasEntry("component", ProfileComponent.DEGREES.toString()))
                                        .and(hasEntry("orderValue", 2))
                        ),
                        hasItem(
                                both(hasEntry("component", ProfileComponent.ATTAINMENTS.toString()))
                                        .and(hasEntry("orderValue", 3))
                        )
                )));
    }

    @Test
    public void thatStudentProfileIsCreatedInSessionLang() throws Exception {
        deleteExistingStudentProfiles();

        createStudentProfile(studentSecurityContext())
                .andExpect(status().isOk())
                .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.url").value(STUDENT_PROFILE_PATH))
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatTeacherProfileIsCreatedInSessionLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherProfile();

        createTeacherProfile(teacherSecurityContext())
                .andExpect(status().isOk())
                .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.url").value(TEACHER_PROFILE_PATH))
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatTeacherProfileIsCreatedWithCorrectContactInfo() throws Exception {
        esbServer.expectEmployeeContactInformationRequest("010540");

        mockMvc.perform(post(TEACHER_PROFILE_API_PATH)
                .with(securityContext(teacherWithoutProfileSecurityContext())))
                .andExpect(jsonPath("$.contactInformation.email").value("olli.opettaja@helsinki.fi"))
                .andExpect(jsonPath("$.contactInformation.workNumber").value("54321"))
                .andExpect(jsonPath("$.contactInformation.workMobile").value("12345678"))
                .andExpect(jsonPath("$.contactInformation.title").value("universitetslektor"))
                .andExpect(jsonPath("$.contactInformation.faculty").value("Käyttäytymistieteellinen tiedekunta"))
                .andExpect(jsonPath("$.contactInformation.financialUnit").value("OIKTDK, Oikeustieteellinen tiedekunta (OIKTDK)"))
                .andExpect(jsonPath("$.contactInformation.workAddress").value("PL 9 (Siltavuorenpenger 1A)"))
                .andExpect(jsonPath("$.contactInformation.workPostcode").value("00014 HELSINGIN YLIOPISTO"));
    }

    @Test
    public void thatStudentCanCreateMultipleProfilesInDifferentLangs() throws Exception {
        deleteExistingStudentProfiles();

        createStudentProfile(studentSecurityContext())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createStudentProfile(studentSecurityContext(), Language.FI)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatStudentCannotCreateMultipleProfilesInSameLang() throws Exception {
        deleteExistingStudentProfiles();

        createStudentProfile(studentSecurityContext())
                .andExpect(status().isOk());

        createStudentProfile(studentSecurityContext())
                .andExpect(status().isInternalServerError());

        createStudentProfile(studentSecurityContext(), Language.EN)
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatTeacherCanCreateMultipleProfilesInDifferentLangs() throws Exception {
        expectEmployeeContactInformationRequestToESB();
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherProfile();

        createTeacherProfile(teacherSecurityContext())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherProfile(teacherSecurityContext(), Language.FI)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatTeacherCannotCreateMultipleProfilesInSameLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        deleteExistingTeacherProfile();

        createTeacherProfile(teacherSecurityContext())
                .andExpect(status().isOk());

        createTeacherProfile(teacherSecurityContext())
                .andExpect(status().isInternalServerError());

        createTeacherProfile(teacherSecurityContext(), Language.EN)
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatHybridUserCanCreateStudentAndTeacherProfilesInSessionLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        createStudentProfile(hybridUserSecurityContext())
                .andExpect(status().isOk())
                .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.url").value(HYBRID_USER_PROFILE_PATH))
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherProfile(hybridUserSecurityContext())
                .andExpect(status().isOk())
                .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.url").value(HYBRID_USER_PROFILE_PATH))
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));
    }

    @Test
    public void thatHybridUserCanCreateMultipleStudentProfilesInDifferentLangs() throws Exception {
        createStudentProfile(hybridUserSecurityContext())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createStudentProfile(hybridUserSecurityContext(), Language.FI)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatHybridUserCannotCreateMultipleStudentProfilesInSameLang() throws Exception {
        createStudentProfile(hybridUserSecurityContext())
                .andExpect(status().isOk());

        createStudentProfile(hybridUserSecurityContext())
                .andExpect(status().isInternalServerError());

        createStudentProfile(hybridUserSecurityContext(), Language.EN)
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatHybridUserCanCreateMultipleTeacherProfilesInDifferentLangs() throws Exception {
        expectEmployeeContactInformationRequestToESB();
        expectEmployeeContactInformationRequestToESB();

        createTeacherProfile(hybridUserSecurityContext())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(SESSION_LANG));

        createTeacherProfile(hybridUserSecurityContext(), Language.FI)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value(Language.FI.getCode()));
    }

    @Test
    public void thatHybridUserCannotCreateMultipleTeacherProfilesInSameLang() throws Exception {
        expectEmployeeContactInformationRequestToESB();

        createTeacherProfile(hybridUserSecurityContext())
                .andExpect(status().isOk());

        createTeacherProfile(hybridUserSecurityContext())
                .andExpect(status().isInternalServerError());

        createTeacherProfile(hybridUserSecurityContext(), Language.EN)
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatProfileIsUpdated() throws Exception {
        String updatedOwnerName = "Olli Opiskelija updated";
        String updatedIntro = "Introtext updated";
        ProfileVisibility updatedVisibility = ProfileVisibility.PUBLIC;

        ProfileDto profileDto = new ProfileDto();
        profileDto.ownerName = updatedOwnerName;
        profileDto.intro = updatedIntro;
        profileDto.visibility = updatedVisibility;

        mockMvc.perform(put(PRIVATE_PROFILE_API_PATH + "/2").with(securityContext(studentSecurityContext()))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(WebTestUtils.toJsonBytes(profileDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.ownerName").value(updatedOwnerName))
                .andExpect(jsonPath("$.intro").value(updatedIntro))
                .andExpect(jsonPath("$.visibility").value(updatedVisibility.name()));

        assertThat(profileService.findById(2L).visibility)
                .isEqualTo(ProfileVisibility.PUBLIC);
    }

    private void deleteExistingStudentProfiles() {
        profileRepository.deleteById(2L);
        profileRepository.deleteById(5L);
    }

    private void deleteExistingTeacherProfile() {
        profileRepository.deleteById(4L);
    }
}
