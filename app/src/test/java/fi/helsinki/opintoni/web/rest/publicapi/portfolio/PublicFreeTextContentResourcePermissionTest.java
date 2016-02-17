package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.repository.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

public class PublicFreeTextContentResourcePermissionTest extends SpringTest {

    private static final String API_URL = "/api/public/v1/portfolio/1/freetextcontent";
    private static final String EXPECTED_TITLE = "Otsikko 2";
    private static final String EXPECTED_TEXT = "Teksti 2";

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    private ResultActions getFreeTextContent(ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(get(API_URL)
            .with(securityContext(teacherSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedResult);
    }

    private void makePortfolioPublic(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        portfolio.visibility = PortfolioVisibility.PUBLIC;
        portfolioRepository.save(portfolio);
    }

    private void makeFreeTextContentPublic(Long portfolioId) {
        ComponentVisibility componentVisibility = new ComponentVisibility();
        componentVisibility.portfolio = portfolioRepository.findOne(portfolioId);
        componentVisibility.component = PortfolioComponent.FREE_TEXT_CONTENT;
        componentVisibility.visibility = ComponentVisibility.Visibility.PUBLIC;
        componentVisibilityRepository.save(componentVisibility);
    }

    @Test
    public void thatUserCannotLoadFreeTextContentOfAnotherUsersPrivatePortfolio() throws Exception{
        getFreeTextContent(status().isNotFound());
    }

    @Test
    public void thatUserCannotLoadFreeTextContentOfAnotherUsersPublicPortfolio() throws Exception{
        makePortfolioPublic(1L);
        getFreeTextContent(status().isForbidden());
    }

    @Test
    public void thatUserCanLoadFreeTextContentOfAnotherUsersPublicPortfolioIfFreeTextContentIsSetToPublicVisibility() throws Exception{
        makePortfolioPublic(1L);
        makeFreeTextContentPublic(1L);
        getFreeTextContent(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value(EXPECTED_TITLE))
            .andExpect(jsonPath("$[0].text").value(EXPECTED_TEXT));
    }


}
