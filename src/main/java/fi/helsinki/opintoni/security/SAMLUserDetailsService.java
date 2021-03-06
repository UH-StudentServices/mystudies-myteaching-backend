/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.security;

import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.service.UserService;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Service;

@Service
@Profile({
    Constants.SPRING_PROFILE_QA,
    Constants.SPRING_PROFILE_PRODUCTION,
    Constants.SPRING_PROFILE_LOCAL_SHIBBO
})
public class SAMLUserDetailsService implements org.springframework.security.saml.userdetails.SAMLUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(SAMLUserDetailsService.class);

    private static final String SAML_ATTRIBUTE_EDU_PERSON_PRINCIPAL_NAME = "urn:oid:1.3.6.1.4.1.5923.1.1.1.6";
    private static final String SAML_ATTRIBUTE_EMAIL = "urn:oid:0.9.2342.19200300.100.1.3";
    private static final String SAML_ATTRIBUTE_COMMON_NAME = "urn:oid:2.5.4.3";
    private static final String SAML_ATTRIBUTE_PERSON_SISU_ID = "urn:oid:1.3.6.1.4.1.18869.1.1.1.48";
    private static final String SAML_ATTRIBUTE_EMPLOYEE_NUMBER = "urn:oid:2.16.840.1.113730.3.1.3";
    private static final String SAML_ATTRIBUTE_STUDENT_NUMBER = "urn:oid:1.3.6.1.4.1.25178.1.2.14";
    private static final String SAML_ATTRIBUTE_TEACHER_FACULTY_CODE = "urn:mace:funet.fi:helsinki.fi:hyAccountingCode";
    private static final String SAML_ATTRIBUTE_PREFERRED_LANGUAGE = "urn:oid:2.16.840.1.113730.3.1.39";
    private static final String PERSONAL_UNIQUE_CODE_UH_STUDENT_ID_PREFIX = "schac:personalUniqueCode:int:studentID:helsinki.fi:";

    private final UserService userService;

    @Autowired
    public SAMLUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        return buildAppUser(credential);
    }

    private Object buildAppUser(SAMLCredential credential) {
        logSAMLCredential(credential);

        AppUser.AppUserBuilder builder = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(credential.getAttributeAsString(SAML_ATTRIBUTE_EDU_PERSON_PRINCIPAL_NAME))
            .email(credential.getAttributeAsString(SAML_ATTRIBUTE_EMAIL))
            .commonName(credential.getAttributeAsString(SAML_ATTRIBUTE_COMMON_NAME))
            .personId(credential.getAttributeAsString(SAML_ATTRIBUTE_PERSON_SISU_ID))
            .studentNumber(getStudentNumber(credential))
            .employeeNumber(credential.getAttributeAsString(SAML_ATTRIBUTE_EMPLOYEE_NUMBER))
            .teacherFacultyCode(credential.getAttributeAsString(SAML_ATTRIBUTE_TEACHER_FACULTY_CODE))
            .preferredLanguage(credential.getAttributeAsString(SAML_ATTRIBUTE_PREFERRED_LANGUAGE));

        if (userService.isAdmin(credential.getAttributeAsString(SAML_ATTRIBUTE_EDU_PERSON_PRINCIPAL_NAME))) {
            builder.role(AppUser.Role.ADMIN);
        }

        return builder.build();
    }

    private void logSAMLCredential(SAMLCredential credential) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Attribute attribute : credential.getAttributes()) {
            sb.append(attribute.getName())
                .append(" with friendlyName ")
                .append(attribute.getFriendlyName())
                .append(" has a value ")
                .append(credential.getAttributeAsString(attribute.getName()))
                .append("\n");
        }
        log.debug("SAMLCredential attributes: " + sb.toString());
    }

    /*
     * Get student number from multiple directory strings, possible values are as follows
     * Valid student personalUniqueCodes:
     * "urn:schac:personalUniqueCode:int:studentID:helsinki.fi:011631484"
     * Valid personalUniqueCodes, but invalid student IDs for University of Helsinki, filter out:
     * "urn:schac:personalUniqueCode:int:studentID:tut.fi:011631484"
     * "urn:schac:personalUniqueCode:se:LIN:87654321"
     */
    private String getStudentNumber(SAMLCredential credential) {
        String studentNumber = null;
        String[] schacPersonalUniqueCodes = credential.getAttributeAsStringArray(SAML_ATTRIBUTE_STUDENT_NUMBER);
        if (schacPersonalUniqueCodes != null) {
            studentNumber = Arrays.asList(schacPersonalUniqueCodes).stream()
                .filter(code -> code.contains(PERSONAL_UNIQUE_CODE_UH_STUDENT_ID_PREFIX))
                .map(code -> StringUtils.substringAfterLast(code, ":"))
                .findAny()
                .orElse(null);
        }
        return studentNumber;
    }
}
