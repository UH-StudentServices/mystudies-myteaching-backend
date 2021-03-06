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

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.TeachingLanguage;
import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.dto.OfficeHoursDtoBuilder;
import fi.helsinki.opintoni.dto.TeachingLanguageDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.ADDITIONAL_INFO;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.ADDITIONAL_INFO_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_1;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.LOCATION;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.LOCATION_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHER_NAME;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHER_NAME_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHING_LANGUAGE_1;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHING_LANGUAGE_2;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfficeHoursResourceTest extends SpringTest {

    private static final LocalDate YEAR_FROM_NOW = LocalDate.now().plusYears(1);
    private static final LocalDate INVALID_EXPIRATION_DATE = LocalDate.now().plusYears(2).plusDays(1);
    private static final String REQUEST_URI = "/api/private/v1/officehours";

    @Test
    public void thatOfficeHoursReturnCorrectResponse() throws Exception {
        mockMvc.perform(get(REQUEST_URI)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].description").value(OFFICE_HOURS))
            .andExpect(jsonPath("$[0].name").value(TEACHER_NAME))
            .andExpect(jsonPath("$[0].additionalInfo").value(ADDITIONAL_INFO))
            .andExpect(jsonPath("$[0].location").value(LOCATION))
            .andExpect(jsonPath("$[0].expirationDate[0]").value("2099"))
            .andExpect(jsonPath("$[0].expirationDate[1]").value("7"))
            .andExpect(jsonPath("$[0].expirationDate[2]").value("31"));
    }

    @Test
    public void thatOfficeHoursUpdateWithBlankDegreeProgrammeCodeIsNotAccepted() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setAdditionalInfo(ADDITIONAL_INFO_2)
            .setLocation(LOCATION_2)
            .setDegreeProgrammes(createProgrammeDtoList("", DEGREE_CODE_2))
            .setLanguages(createLanguageDtoList()).setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void thatOfficeHoursUpdateSucceedsWithCodeOfRealisticLength() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setAdditionalInfo(ADDITIONAL_INFO_2)
            .setLocation(LOCATION_2)
            .setDegreeProgrammes(createProgrammeDtoList("KH60_001 SH60_035", DEGREE_CODE_2))
            .setLanguages(createLanguageDtoList()).setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isOk());
    }

    @Test
    public void thatOfficeHoursAreUpdated() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setAdditionalInfo(ADDITIONAL_INFO_2)
            .setLocation(LOCATION_2)
            .setDegreeProgrammes(createProgrammeDtoList(DEGREE_CODE_1, DEGREE_CODE_2))
            .setLanguages(createLanguageDtoList())
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].description").value(OFFICE_HOURS))
            .andExpect(jsonPath("$[0]name").value(TEACHER_NAME))
            .andExpect(jsonPath("$[0]additionalInfo").value(ADDITIONAL_INFO_2))
            .andExpect(jsonPath("$[0]location").value(LOCATION_2))
            .andExpect(jsonPath("$[0]degreeProgrammes").isArray())
            .andExpect(jsonPath("$[0]degreeProgrammes", hasSize(2)))
            .andExpect(jsonPath("$[0]degreeProgrammes[0].code").value(DEGREE_CODE_1))
            .andExpect(jsonPath("$[0]degreeProgrammes[1].code").value(DEGREE_CODE_2));
    }

    @Test
    public void thatOfficeHoursAreDeleted() throws Exception {
        mockMvc.perform(delete(REQUEST_URI)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    public void thatMultipleOfficeHoursCanBeAdded() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setDegreeProgrammes(createProgrammeDtoList(DEGREE_CODE_1, DEGREE_CODE_2))
            .setLanguages(createLanguageDtoList())
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        OfficeHoursDto officeHoursDto2 = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME_2)
            .setDescription(OFFICE_HOURS_2)
            .setDegreeProgrammes(createProgrammeDtoList())
            .setLanguages(createLanguageDtoList(TEACHING_LANGUAGE_2))
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto, officeHoursDto2)
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].description").value(OFFICE_HOURS))
            .andExpect(jsonPath("$[0]name").value(TEACHER_NAME))
            .andExpect(jsonPath("$[0]degreeProgrammes").isArray())
            .andExpect(jsonPath("$[0]degreeProgrammes", hasSize(2)))
            .andExpect(jsonPath("$[0]degreeProgrammes[0].code").value(DEGREE_CODE_1))
            .andExpect(jsonPath("$[0]degreeProgrammes[1].code").value(DEGREE_CODE_2))
            .andExpect(jsonPath("$[1].description").value(OFFICE_HOURS_2))
            .andExpect(jsonPath("$[1]name").value(TEACHER_NAME_2))
            .andExpect(jsonPath("$[1]languages").isArray())
            .andExpect(jsonPath("$[1]languages", hasSize(1)))
            .andExpect(jsonPath("$[1]languages[0].code").value(TEACHING_LANGUAGE_2))
            .andExpect(jsonPath("$[1]languages[0].name.fi").value(TeachingLanguage.fromCode(TEACHING_LANGUAGE_2).getNameFor("fi")));
    }

    @Test
    public void thatExpirationDateCantBeMoreThanYearFromNow() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setDegreeProgrammes(createProgrammeDtoList(DEGREE_CODE_1, DEGREE_CODE_2))
            .setLanguages(createLanguageDtoList())
            .setExpirationDate(INVALID_EXPIRATION_DATE)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void thatOfficeHoursWithTeachingLanguageIsSaved() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setDegreeProgrammes(createProgrammeDtoList())
            .setLanguages(createLanguageDtoList(TEACHING_LANGUAGE_1))
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].languages").isArray())
            .andExpect(jsonPath("$[0].languages", hasSize(1)))
            .andExpect(jsonPath("$[0].languages[0].code").value(TEACHING_LANGUAGE_1))
            .andExpect(jsonPath("$[0].languages[0].name.fi").value(TeachingLanguage.fromCode(TEACHING_LANGUAGE_1).getNameFor("fi")));
    }

    @Test
    public void thatOfficeHoursWithInvalidTeachingLanguageCodeIsNotAccepted() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setDegreeProgrammes(createProgrammeDtoList())
            .setLanguages(ImmutableList.of(new TeachingLanguageDto("no_such_language", Collections.emptyMap())))
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void thatOfficeHoursWithDegreeProgrammesAndTeachingLanguagesIsNotAccepted() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setDegreeProgrammes(createProgrammeDtoList(DEGREE_CODE_1))
            .setLanguages(createLanguageDtoList(TEACHING_LANGUAGE_1))
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isUnprocessableEntity());
    }

    //This is accepted, because there may exist old data that has not had degree programmes and Teaching languages set.
    //This data must be able to be fixed by the user via UI. UI validates, that it is not possible to update existing office hours
    //or create new ones without having either degree programmes or teaching languages set.
    @Test
    public void thatOfficeHoursWithoutDegreeProgrammesAndTeachingLanguagesIsAccepted() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDtoBuilder()
            .setName(TEACHER_NAME)
            .setDescription(OFFICE_HOURS)
            .setExpirationDate(YEAR_FROM_NOW)
            .createOfficeHoursDto();

        performPostOfficeHours(officeHoursDto)
            .andExpect(status().isOk());
    }

    @Test
    public void thatTeachingLanguagesAreReturned() throws Exception {
        ResultActions result = mockMvc.perform(get(String.join("/", REQUEST_URI, "teaching-languages"))
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(TeachingLanguage.values().length)));

        int idx = 0;
        for (TeachingLanguage lang : TeachingLanguage.values()) {
            result.andExpect(jsonPath(String.format("$.[%d].code", idx)).value(lang.getCode()));
            for (String langCode : Language.getCodes()) {
                result.andExpect(jsonPath(String.format("$.[%d].name.%s", idx, langCode)).value(lang.getNameFor(langCode)));
            }
            idx++;
        }
    }

    private List<DegreeProgrammeDto> createProgrammeDtoList(String... codes) {
        return Arrays.stream(codes).map(code -> {
            DegreeProgrammeDto dto = new DegreeProgrammeDto();
            dto.code = code;
            return dto;
        }).collect(Collectors.toList());
    }

    private List<TeachingLanguageDto> createLanguageDtoList(String... codes) {
        return Arrays.stream(codes)
            .map(code -> TeachingLanguage.fromCode(code).toDto())
            .collect(Collectors.toList());
    }

    private InsertOfficeHoursRequest createInsertOfficeHoursRequest(OfficeHoursDto... officeHoursDtos) {
        return new InsertOfficeHoursRequest(ImmutableList.copyOf(officeHoursDtos));
    }

    private ResultActions performPostOfficeHours(OfficeHoursDto... officeHoursDtos) throws Exception {
        return mockMvc.perform(post(REQUEST_URI)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(createInsertOfficeHoursRequest(officeHoursDtos)))
            .accept(MediaType.APPLICATION_JSON));
    }
}
