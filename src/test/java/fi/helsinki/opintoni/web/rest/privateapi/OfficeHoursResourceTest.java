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
import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.*;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OfficeHoursResourceTest extends SpringTest {

    private static final LocalDate YEAR_FROM_NOW = LocalDate.now().plusYears(1);
    private static final LocalDate INVALID_EXPIRATION_DATE = LocalDate.now().plusYears(2).plusDays(1);

    @Test
    public void thatOfficeHoursReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].description").value(OFFICE_HOURS))
            .andExpect(jsonPath("$[0].name").value(TEACHER_NAME))
            .andExpect(jsonPath("$[0].additionalInfo").value(ADDITIONAL_INFO))
            .andExpect(jsonPath("$[0].location").value(LOCATION))
            .andExpect(jsonPath("$[0].expirationDate[0]").value("2019"))
            .andExpect(jsonPath("$[0].expirationDate[1]").value("7"))
            .andExpect(jsonPath("$[0].expirationDate[2]").value("31"));
    }

    @Test
    public void thatOfficeHoursUpdateFailsWithCorrectStatus() throws Exception {
        DegreeProgrammeDto programme1 = new DegreeProgrammeDto();
        DegreeProgrammeDto programme2 = new DegreeProgrammeDto();
        programme1.code = ""; //blank not allowed
        programme2.code = DEGREE_CODE_2;

        OfficeHoursDto officeHoursDto = new OfficeHoursDto(TEACHER_NAME, OFFICE_HOURS,
            ADDITIONAL_INFO_2, LOCATION_2, Arrays.asList(programme1, programme2), YEAR_FROM_NOW);

        List<OfficeHoursDto> request = Arrays.asList(officeHoursDto);

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void thatOfficeHoursUpdateSucceedsWithCodeOfRealisticLength() throws Exception {
        DegreeProgrammeDto programme1 = new DegreeProgrammeDto();
        DegreeProgrammeDto programme2 = new DegreeProgrammeDto();
        programme1.code = "KH60_001 SH60_035";
        programme2.code = DEGREE_CODE_2;
        OfficeHoursDto officeHoursDto = new OfficeHoursDto(TEACHER_NAME, OFFICE_HOURS,
            ADDITIONAL_INFO_2, LOCATION_2, ImmutableList.of(programme1, programme2), YEAR_FROM_NOW);

        InsertOfficeHoursRequest request = new InsertOfficeHoursRequest(ImmutableList.of(officeHoursDto));

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void thatOfficeHoursAreUpdated() throws Exception {
        DegreeProgrammeDto programme1 = new DegreeProgrammeDto();
        DegreeProgrammeDto programme2 = new DegreeProgrammeDto();
        programme1.code = DEGREE_CODE_1;
        programme2.code = DEGREE_CODE_2;
        OfficeHoursDto officeHoursDto = new OfficeHoursDto(TEACHER_NAME, OFFICE_HOURS,
            ADDITIONAL_INFO_2, LOCATION_2, Arrays.asList(programme1, programme2), YEAR_FROM_NOW);

        InsertOfficeHoursRequest request = new InsertOfficeHoursRequest(Arrays.asList(officeHoursDto));

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(delete("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    public void thatMultipleOfficeHoursCanBeAdded() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDto(
            TEACHER_NAME,
            OFFICE_HOURS,
            null,
            null,
            createProgrammeDtoList(DEGREE_CODE_1, DEGREE_CODE_2),
            YEAR_FROM_NOW
        );

        OfficeHoursDto officeHoursDto2 = new OfficeHoursDto(
            TEACHER_NAME_2,
            OFFICE_HOURS_2,
            null,
            null,
            createProgrammeDtoList(DEGREE_CODE_3),
            YEAR_FROM_NOW
        );

        InsertOfficeHoursRequest request = new InsertOfficeHoursRequest(Arrays.asList(officeHoursDto, officeHoursDto2));

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
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
            .andExpect(jsonPath("$[1]degreeProgrammes").isArray())
            .andExpect(jsonPath("$[1]degreeProgrammes", hasSize(1)))
            .andExpect(jsonPath("$[1]degreeProgrammes[0].code").value(DEGREE_CODE_3));
    }

    @Test
    public void thatExpirationDateCantBeMoreThanYearFromNow() throws Exception {
        OfficeHoursDto officeHoursDto = new OfficeHoursDto(
            TEACHER_NAME,
            OFFICE_HOURS,
            null,
            null,
            createProgrammeDtoList(DEGREE_CODE_1, DEGREE_CODE_2),
            INVALID_EXPIRATION_DATE
        );

        InsertOfficeHoursRequest invalidRequest = new InsertOfficeHoursRequest(Arrays.asList(officeHoursDto));

        mockMvc.perform(post("/api/private/v1/officehours")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJsonBytes(invalidRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    private List<DegreeProgrammeDto> createProgrammeDtoList(String... codes) {
        return Arrays.stream(codes).map(code -> {
            DegreeProgrammeDto dto = new DegreeProgrammeDto();
            dto.code = code;
            return dto;
        }).collect(Collectors.toList());
    }
}
