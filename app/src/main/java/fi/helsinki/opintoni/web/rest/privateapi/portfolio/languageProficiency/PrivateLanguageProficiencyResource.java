package fi.helsinki.opintoni.web.rest.privateapi.portfolio.languageProficiency;

import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguageProficiency;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<LanguageProficiencyDto> addLanguageProficiency(@PathVariable Long portfolioId,
                                                                         @RequestBody LanguageProficiencyDto languageProficiencyDto) {
        return response(languageProficiencyService.addLanguageProficiency(portfolioId, languageProficiencyDto));
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{languageProficiencyId}")
    public ResponseEntity<LanguageProficiencyDto> updateLanguageProficiency(@UserId Long userId,
                                                                            @PathVariable Long languageProficiencyId,
                                                                            @RequestBody LanguageProficiencyDto languageProficiencyDto) {
        permissionChecker.verifyPermission(userId, languageProficiencyId, PortfolioLanguageProficiency.class);

        return response(languageProficiencyService.updateLanguageProficiency(languageProficiencyId, languageProficiencyDto));
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{languageProficiencyId}")
    public ResponseEntity deleteLanguageProficiency(@UserId Long userId, @PathVariable Long languageProficiencyId) {
        permissionChecker.verifyPermission(userId, languageProficiencyId, PortfolioLanguageProficiency.class);
        languageProficiencyService.deleteLanguageProficiency(languageProficiencyId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
