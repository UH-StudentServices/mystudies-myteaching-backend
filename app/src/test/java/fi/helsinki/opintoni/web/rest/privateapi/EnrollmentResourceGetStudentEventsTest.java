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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import java.util.Locale;

import static fi.helsinki.opintoni.config.Constants.NG_TRANSLATE_LANG_KEY;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EnrollmentResourceGetStudentEventsTest extends SpringTest {
    private static final String LANG_CODE_FI = Language.FI.getCode();
    private static final String LANG_CODE_EN = Language.EN.getCode();
    private static final String LANG_CODE_SV = Language.SV.getCode();
    private static final String INVALID_LANGUAGE_CODE = "invalidLangCode";
    private static final String UNSUPPORTED_LANG_CODE = "de";
    private static final String LOCALE_LANGUAGE_FORMAT = "fi_FI";

    private static final String EVENT_TITLE_FI = "Formuloi... Harjoitus II";
    private static final String EVENT_TITLE_SV = "Formuler... Harjoitus II (sv)";
    private static final String EVENT_TITLE_EN = "Formulat... Harjoitus II (en)";

    private ResultActions performGetStudentEvents(
        String cookieLanguage,
        String acceptLanguageHeader,
        String expectedResolvedLanguage,
        String expectedFirstEventTitle) throws Exception {

        expectEvents(expectedResolvedLanguage);

        MockHttpServletRequestBuilder requestBuilder = get("/api/private/v1/students/enrollments/events")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON);

        if(cookieLanguage != null) {
            requestBuilder.cookie(new Cookie(NG_TRANSLATE_LANG_KEY, cookieLanguage));
        }

        if(acceptLanguageHeader != null) {
            requestBuilder.header(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguageHeader);
        }

        ResultActions result = mockMvc.perform(requestBuilder);

        return result.andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].title").value(expectedFirstEventTitle));
    }

    @Test
    public void thatStudentEventsAreReturnedInFinnish() throws Exception {
        performGetStudentEvents(LANG_CODE_FI, null, LANG_CODE_FI, EVENT_TITLE_FI)
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].realisationId").value(123456789))
            .andExpect(jsonPath("$[0].endDate").isArray())
            .andExpect(jsonPath("$[0].endDate[0]").value(2016))
            .andExpect(jsonPath("$[0].endDate[1]").value(12))
            .andExpect(jsonPath("$[0].endDate[2]").value(19))
            .andExpect(jsonPath("$[0].endDate[3]").value(15))
            .andExpect(jsonPath("$[0].endDate[4]").value(45))
            .andExpect(jsonPath("$[0].startDate").isArray())
            .andExpect(jsonPath("$[0].startDate[0]").value(2016))
            .andExpect(jsonPath("$[0].startDate[1]").value(12))
            .andExpect(jsonPath("$[0].startDate[2]").value(19))
            .andExpect(jsonPath("$[0].startDate[3]").value(14))
            .andExpect(jsonPath("$[0].startDate[4]").value(15))
            .andExpect(jsonPath("$[0].title").value(EVENT_TITLE_FI))
            .andExpect(jsonPath("$[0].type").value(EventDto.Type.DEFAULT.name()))
            .andExpect(jsonPath("$[0].source").value(EventDto.Source.OODI.name()))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialUri")
                .value("https://dev.student.helsinki.fi/tvt?group-imp-material"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialType")
                .value(CourseMaterialDto.CourseMaterialType.COURSE_PAGE.toString()))
            .andExpect(jsonPath("$[0].locations[0].locationString").value("Päärakennus, sali 1, Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].roomName").value("Päärakennus, sali 1"))
            .andExpect(jsonPath("$[0].locations[0].streetAddress").value("Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].zipCode").value("00790"))
            .andExpect(jsonPath("$[0].hasMaterial").value(true))
            .andExpect(jsonPath("$[3].title").value("Ei päättymisaikaa"))
            .andExpect(jsonPath("$[3].source").value(EventDto.Source.COURSE_PAGE.name()))
            .andExpect(jsonPath("$[3].type").value(EventDto.Type.DEFAULT.name()));
    }

    @Test
    public void thatStudentEventsAreReturnedInSwedish() throws Exception {
        performGetStudentEvents(LANG_CODE_SV, null, LANG_CODE_SV, EVENT_TITLE_SV);
    }

    @Test
    public void thatStudentEventsAreReturnedInEnglish() throws Exception {
        performGetStudentEvents(LANG_CODE_EN, null, LANG_CODE_EN, EVENT_TITLE_EN);
    }

    @Test
    public void thatLocaleFormatInCookieWillResolveToDefaultLanguage() throws Exception {
        performGetStudentEvents(LOCALE_LANGUAGE_FORMAT, null, DEFAULT_USER_LOCALE.getLanguage(), EVENT_TITLE_EN);
    }

    @Test
    public void thatInvalidLanguageCodeWillResolveToDefaultLanguage() throws Exception {
        performGetStudentEvents(INVALID_LANGUAGE_CODE, null, DEFAULT_USER_LOCALE.getLanguage(), EVENT_TITLE_EN);
    }

    @Test
    public void thatUnsupportedLanguageCodeWillResolveToDefaultLanguage() throws Exception {
        performGetStudentEvents(UNSUPPORTED_LANG_CODE, null, DEFAULT_USER_LOCALE.getLanguage(), EVENT_TITLE_EN);
    }

    @Test
    public void thatAcceptLanguageHeaderIsAlwaysResolvedToDefaultLanguage() throws Exception {
        performGetStudentEvents(null, LANG_CODE_SV, DEFAULT_USER_LOCALE.getLanguage(), EVENT_TITLE_EN);
    }

    @Test
    public void thatIfLanguageCookieIsNotPresentDefaultLanguageIsUsed() throws Exception {
        performGetStudentEvents(null, null, DEFAULT_USER_LOCALE.getLanguage(), EVENT_TITLE_EN);
    }

    private void expectEvents(String langCode) {
        defaultStudentRequestChain()
            .events()
            .defaultImplementationWithLocale(new Locale(langCode))
            .and()
            .enrollments();
    }

}

