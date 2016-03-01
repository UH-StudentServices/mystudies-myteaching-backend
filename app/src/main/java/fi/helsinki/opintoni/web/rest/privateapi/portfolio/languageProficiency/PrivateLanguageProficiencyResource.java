package fi.helsinki.opintoni.web.rest.privateapi.portfolio.languageProficiency;

import fi.helsinki.opintoni.dto.portfolio.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/languageproficiencies",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateLanguageProficiencyResource extends AbstractResource {
    private final PermissionChecker permissionChecker;
    private final LanguageProficiencyService languageProficiencyService;

    @Autowired
    public PrivateLanguageProficiencyResource(PermissionChecker permissionChecker,
                                              LanguageProficiencyService languageProficiencyService) {
        this.permissionChecker = permissionChecker;
        this.languageProficiencyService = languageProficiencyService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<LanguageProficiencyDto>> findByPortfolioId(@PathVariable Long portfolioId) {
        return response(languageProficiencyService.findByPortfolioId(portfolioId));
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity updateLanguageProficiencies(@UserId Long userId,
                                                      @PathVariable Long portfolioId,
                                                      @RequestBody LanguageProficienciesChangeDescriptorDto changeDescriptor) {
        languageProficiencyService.updateLanguageProficiencies(changeDescriptor, portfolioId, userId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
