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
import fi.helsinki.opintoni.domain.portfolio.SkillsAndExpertise;
import fi.helsinki.opintoni.repository.portfolio.SkillsAndExpertiseRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.skillsandexpertise.UpdateSkillsAndExpertiseRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateSkillsAndExpertiseResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/skillsandexpertise";
    private static final Long PORTFOLIO_ID = 2L;
    private static final String NEW_SKILLS_AND_EXPERTISE = "New skills and expertise content";

    @Autowired
    private SkillsAndExpertiseRepository skillsAndExpertiseRepository;

    @Test
    public void thatSkillsAndExpertiseIsUpdated() throws Exception {
        UpdateSkillsAndExpertiseRequest request = new UpdateSkillsAndExpertiseRequest();
        request.skillsAndExpertise = NEW_SKILLS_AND_EXPERTISE;

        mockMvc.perform(put(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk());

        Optional<SkillsAndExpertise> skillsAndExpertise = skillsAndExpertiseRepository.findByPortfolioId(PORTFOLIO_ID);

        assertThat(skillsAndExpertise.isPresent()).isTrue();
        assertThat(skillsAndExpertise.get().skillsAndExpertise).isEqualTo(NEW_SKILLS_AND_EXPERTISE);
    }
}
