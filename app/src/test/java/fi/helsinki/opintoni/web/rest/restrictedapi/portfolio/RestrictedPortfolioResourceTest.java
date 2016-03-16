package fi.helsinki.opintoni.web.rest.restrictedapi.portfolio;

import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestrictedPortfolioResourceTest extends RestrictedPortfolioTest {
    private static final String STUDENT_PORTFOLIO_PATH = "/portfolio/student/olli-opiskelija";

    @Test
    public void thatPortfolioIsReturned() throws Exception {
        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    public void thatPortfolioContainsNoLocallyStoredPrivateComponents() throws Exception {
        setPrivateVisibilitiesForEveryComponent();

        mockMvc.perform(get(RestConstants.RESTRICTED_API_V1 + STUDENT_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contactInformation").isEmpty())
            .andExpect(jsonPath("$.degrees").isEmpty())
            .andExpect(jsonPath("$.workExperience").isEmpty())
            .andExpect(jsonPath("$.jobSearch").isEmpty())
            .andExpect(jsonPath("$.freeTextContent").isEmpty())
            .andExpect(jsonPath("$.languageProficiencies").isEmpty())
            .andExpect(jsonPath("$.keywords").isEmpty())
            .andExpect(jsonPath("$.summary").isEmpty())
            .andExpect(jsonPath("$.favorites").isEmpty());
    }
}
