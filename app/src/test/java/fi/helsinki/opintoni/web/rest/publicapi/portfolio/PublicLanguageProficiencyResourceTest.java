package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicLanguageProficiencyResourceTest extends PublicPortfolioTest {
    private static final String SUBRESOURCE_PATH = "/languageproficiencies";

    @Test
    public void thatLanguageProficienciesAreReturned() throws Exception {
        mockMvc.perform(get(PUBLIC_PORTFOLIO_URL + SUBRESOURCE_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }
}
