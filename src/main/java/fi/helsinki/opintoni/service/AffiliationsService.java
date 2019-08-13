package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.AffiliationsDto;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.service.converter.FacultyConverter;
import org.springframework.stereotype.Service;

import static fi.helsinki.opintoni.exception.http.ForbiddenException.forbiddenException;

@Service
public class AffiliationsService {

    private final OodiUserService oodiUserService;
    private final FacultyConverter facultyConverter;
    private final SecurityUtils securityUtils;

    public AffiliationsService(OodiUserService oodiUserService,
                               FacultyConverter facultyConverter,
                               SecurityUtils securityUtils) {
        this.oodiUserService = oodiUserService;
        this.facultyConverter = facultyConverter;
        this.securityUtils = securityUtils;
    }

    public AffiliationsDto getAffiliations() {
        return securityUtils.getAppUser().map(appUser -> {
            AffiliationsDto affiliationsDto = new AffiliationsDto();
            affiliationsDto.openUniversity = oodiUserService.isOpenUniversityUser(appUser);
            affiliationsDto.faculty = facultyConverter.getFacultyDto(appUser);
            return affiliationsDto;
        }).orElseThrow(forbiddenException("No session"));
    }
}
