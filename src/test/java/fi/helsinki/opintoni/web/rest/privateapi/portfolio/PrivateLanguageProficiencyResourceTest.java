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

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateLanguageProficiencyResourceTest extends SpringTest {
    private static final String API_PATH = "/profile/2/languageproficiencies";

    private static final long ENGLISH_LANGUAGE_PROFICIENCY_ID = 1L;
    private static final long HINDI_LANGUAGE_PROFICIENCY_ID = 4L;
    private static final long ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID = 3L;

    private static final String CHINESE = "Chinese";
    private static final String ENGLISH = "English";
    private static final String FINNISH = "Finnish";
    private static final String GREEK = "Greek";
    private static final String SWEDISH = "Swedish";

    private static final String ELEMENTARY_PROFICIENCY = "Elementary ";
    private static final String FULL_PROFESSIONAL_PROFICIENCY = "Full professional";
    private static final String LIMITED_WORKING_PROFICIENCY = "Limited working";
    private static final String NATIVE_PROFICIENCY = "Native";

    @Test
    public void thatLanguageProficiencyChangesArePersisted() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();

        LanguageProficiencyDto greek = newLanguageProficiency(GREEK,
                ELEMENTARY_PROFICIENCY,
                "",
                null);
        LanguageProficiencyDto chinese = newLanguageProficiency(CHINESE,
                LIMITED_WORKING_PROFICIENCY,
                "",
                null);

        updateBatch.newLanguageProficiencies = ImmutableList.of(greek, chinese);

        LanguageProficiencyDto english = newLanguageProficiency(ENGLISH,
                NATIVE_PROFICIENCY,
                "",
                ENGLISH_LANGUAGE_PROFICIENCY_ID);

        updateBatch.updatedLanguageProficiencies = ImmutableList.of(english);
        updateBatch.deletedIds = ImmutableList.of(HINDI_LANGUAGE_PROFICIENCY_ID);

        persistUpdateBatchWithStatus(updateBatch, jsonPath("$").value(Matchers.<List<LanguageProficiencyDto>>allOf(
                hasSize(4),
                hasItem(
                        both(hasEntry("id", 1)).and(hasEntry("languageName", ENGLISH)).and(hasEntry("proficiency", NATIVE_PROFICIENCY))
                ),
                hasItem(
                        both(hasEntry("id", 2)).and(hasEntry("languageName", FINNISH)).and(hasEntry("proficiency", NATIVE_PROFICIENCY))
                ),
                hasItem(
                        both(hasEntry("id", 5)).and(hasEntry("languageName", GREEK)).and(hasEntry("proficiency", ELEMENTARY_PROFICIENCY))
                ),
                hasItem(
                        both(hasEntry("id", 6)).and(hasEntry("languageName", CHINESE)).and(hasEntry("proficiency", LIMITED_WORKING_PROFICIENCY))
                )
        )));
    }

    @Test
    public void thatUserCannotUpdateLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();
        LanguageProficiencyDto swedish = newLanguageProficiency(SWEDISH,
                FULL_PROFESSIONAL_PROFICIENCY,
                "",
                ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID);

        updateBatch.updatedLanguageProficiencies = ImmutableList.of(swedish);

        persistUpdateBatchWithStatus(updateBatch, status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();
        updateBatch.deletedIds = ImmutableList.of(ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID);

        persistUpdateBatchWithStatus(updateBatch, status().isForbidden());
    }

    private void persistUpdateBatchWithStatus(LanguageProficienciesChangeDescriptorDto updateBatch,
                                              ResultMatcher status) throws Exception {
        mockMvc.perform(patch(RestConstants.PRIVATE_API_V1 + API_PATH)
                .with(securityContext(studentSecurityContext()))
                .content(toJsonBytes(updateBatch))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status);
    }

    private LanguageProficiencyDto newLanguageProficiency(String languageName,
                                                          String proficiency,
                                                          String description,
                                                          Long id) {
        LanguageProficiencyDto dto = new LanguageProficiencyDto();
        dto.languageName = languageName;
        dto.proficiency = proficiency;
        dto.description = description;
        dto.id = id;

        return dto;
    }
}
