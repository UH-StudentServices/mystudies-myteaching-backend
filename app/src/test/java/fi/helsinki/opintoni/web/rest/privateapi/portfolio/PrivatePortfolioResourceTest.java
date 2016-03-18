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
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.portfolio.DegreeDto;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.dto.portfolio.KeywordDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PrivatePortfolioResourceTest extends SpringTest {

    private static final String STUDENT_PORTFOLIO_API_URL = "/portfolio/student";

    private static final String STUDENT_EMAIL = "olli.opiskelija@helsinki.fi";

    @Test
    public void thatPortfolioIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PRIVATE_API_V1 + STUDENT_PORTFOLIO_API_URL)
            .with(securityContext(studentSecurityContext())))
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    public void thatPortfolioContainsAllLocallyStoredComponents() throws Exception {
        mockMvc.perform(get(RestConstants.PRIVATE_API_V1 + STUDENT_PORTFOLIO_API_URL)
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
                    both(
                        hasEntry("employer", "HY")).and(
                        hasEntry("jobTitle", "Harjoittelija")
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
}
