package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateLanguageProficiencyPermissionsTest extends SpringTest {
    private static final String PORTFOLIO_PATH = "/portfolio/2/languageproficiencies";
    private static final String FORBIDDEN_LANGUAGE_PROFICIENCY_PATH = "/3";

    @Test
    public void thatUserCannotUpdateLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        mockMvc.perform(put(RestConstants.PRIVATE_API_V1 + PORTFOLIO_PATH + FORBIDDEN_LANGUAGE_PROFICIENCY_PATH)
            .content(toJsonBytes(new LanguageProficiencyDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + PORTFOLIO_PATH + FORBIDDEN_LANGUAGE_PROFICIENCY_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }
}
