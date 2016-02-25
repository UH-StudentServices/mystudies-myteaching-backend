package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.security.authorization.portfolio.PublicVisibility;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/languageproficiencies",
    produces = WebConstants.APPLICATION_JSON_UTF8)
@PublicVisibility(PortfolioComponent.LANGUAGE_PROFICIENCY)
public class PublicLanguageProficiencyResource extends AbstractResource {
    private final LanguageProficiencyService languageProficiencyService;

    @Autowired
    public PublicLanguageProficiencyResource(LanguageProficiencyService languageProficiencyService) {
        this.languageProficiencyService = languageProficiencyService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<LanguageProficiencyDto>> findByPortfolioId(@PathVariable Long portfolioId) {
        return response(languageProficiencyService.findByPortfolioId(portfolioId));
    }
}
