package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.security.authorization.portfolio.PublicVisibility;
import fi.helsinki.opintoni.service.portfolio.FreeTextContentService;
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
    value = RestConstants.PUBLIC_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/freetextcontent",
    produces = WebConstants.APPLICATION_JSON_UTF8)
@PublicVisibility(PortfolioComponent.FREE_TEXT_CONTENT)
public class PublicFreeTextContentResource extends AbstractResource {

    private final FreeTextContentService freeTextContentService;

    @Autowired
    public PublicFreeTextContentResource(FreeTextContentService freeTextContentService) {
        this.freeTextContentService = freeTextContentService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FreeTextContentDto>> findByPortfolioId(@PathVariable Long portfolioId) {
        return response(freeTextContentService.findByPortfolioId(portfolioId));
    }

}
