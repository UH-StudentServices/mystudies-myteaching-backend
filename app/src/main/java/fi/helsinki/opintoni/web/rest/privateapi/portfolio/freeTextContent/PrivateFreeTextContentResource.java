package fi.helsinki.opintoni.web.rest.privateapi.portfolio.freeTextContent;

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.portfolio.FreeTextContentService;
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

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/freetextcontent",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateFreeTextContentResource extends AbstractResource {

    private final PermissionChecker permissionChecker;
    private final FreeTextContentService freeTextContentService;

    @Autowired
    public PrivateFreeTextContentResource(PermissionChecker permissionChecker, FreeTextContentService freeTextContentService) {
        this.permissionChecker = permissionChecker;
        this.freeTextContentService = freeTextContentService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FreeTextContentDto> insertFreeTextContent(@PathVariable Long portfolioId,
                                                                    @RequestBody FreeTextContentDto freeTextContentDto) {
        return response(freeTextContentService.insertFreeTextContent(portfolioId, freeTextContentDto));
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{freeTextContentId}")
    public ResponseEntity<FreeTextContentDto> updateFreeTextContent(@UserId Long userId,
                                                @PathVariable Long freeTextContentId,
                                                @RequestBody FreeTextContentDto freeTextContentDto) {
        permissionChecker.verifyPermission(userId, freeTextContentId, FreeTextContent.class);
        return response(freeTextContentService.updateFreeTextContent(freeTextContentId, freeTextContentDto));
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{freeTextContentId}")
    public ResponseEntity deleteFreeTextContent(@UserId Long userId,
                                                @PathVariable Long freeTextContentId) {
        permissionChecker.verifyPermission(userId, freeTextContentId, FreeTextContent.class);
        freeTextContentService.deleteFreeTextContent(freeTextContentId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
