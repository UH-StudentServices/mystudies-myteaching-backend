package fi.helsinki.opintoni.web.rest.privateapi;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.AffiliationsDto;
import fi.helsinki.opintoni.service.AffiliationsService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.PRIVATE_API_V1)
public class AffiliationResource extends AbstractResource {

    private final AffiliationsService affiliationsService;

    @Autowired
    public AffiliationResource(AffiliationsService affiliationsService) {
        this.affiliationsService = affiliationsService;
    }

    @RequestMapping(value = "/affiliations",
        method = RequestMethod.GET,
        produces = WebConstants.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<AffiliationsDto> getAffiliations() {
        return response(affiliationsService.getAffiliations());
    }
}

