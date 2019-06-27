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

package fi.helsinki.opintoni.web.rest.publicapi.profile;

import fi.helsinki.opintoni.domain.profile.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.repository.profile.ProfileStudyAttainmentWhitelistRepository;
import fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicProfileAttainmentResourceTest extends PublicProfileTest {

    @Autowired
    private ProfileStudyAttainmentWhitelistRepository whitelistRepository;

    @Test
    public void thatAttainmentsAreReturned() throws Exception {
        defaultStudentRequestChain().roles().attainments();

        mockMvc.perform(get(PUBLIC_STUDENT_PROFILE_API_PATH + "/attainment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void thatGradesAreReturnedWhenConfigured() throws Exception {
        defaultStudentRequestChain().roles().attainments();

        mockMvc.perform(get(PUBLIC_STUDENT_PROFILE_API_PATH + "/attainment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].grade").value(StudyAttainmentSampleData.GRADE));
    }

    @Test
    public void thatGradesAreNotReturnedWhenNotConfigured() throws Exception {

        StudyAttainmentWhitelist whitelist = whitelistRepository.findByProfileId(STUDENT_PROFILE_ID).get();
        whitelist.showGrades = false;
        whitelistRepository.save(whitelist);

        defaultStudentRequestChain().roles().attainments();

        mockMvc.perform(get(PUBLIC_STUDENT_PROFILE_API_PATH + "/attainment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].grade").isEmpty());
    }
}
