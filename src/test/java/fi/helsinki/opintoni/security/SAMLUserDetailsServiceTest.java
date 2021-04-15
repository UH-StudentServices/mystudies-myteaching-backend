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

import com.google.common.collect.Iterables;
import fi.helsinki.opintoni.service.UserService;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SAMLUserDetailsServiceTest {

    private static final String SAML_PERSONAL_UNIQUE_CODE_FIELD = "urn:oid:1.3.6.1.4.1.25178.1.2.14";
    private static final String SAML_EMAIL = "email";
    private static final String SAML_COMMON_NAME = "commonName";
    private static final String SAML_PRINCIPAL_NAME = "eduPersonPrincipalName";
    private static final String SAML_STUDENT_NUMBER =
        "urn:schac:personalUniqueCode:int:studentID:helsinki.fi:011631484";
    private static final String SAML_OTHER_UNIVERSITY_STUDENT_NUMBER =
        "urn:schac:personalUniqueCode:int:studentID:tut.fi:011631484";
    private static final String SAML_EMPLOYEE_PERSONAL_UNIQUE_CODE =
        "urn:schac:personalUniqueCode:se:LIN:112435";
    private static final String SAML_STUDENT_NUMBER_FINAL = "011631484";
    private static final String SAML_EMPLOYEE_NUMBER = "employeeNumber";
    private static final String SISU_PERSON_ID = "hy-hlo-1440748";
    private static final String SAML_PREFERRED_LANGUAGE = "fi";

    private final UserService userService = mock(UserService.class);

    private SAMLUserDetailsService userDetailsService = new SAMLUserDetailsService(userService);

    @Test
    public void thatStudentAppUserIsReturned() {
        SAMLCredential credential = samlStudentCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertThat(appUser.getUsername()).isEqualTo(SAML_PRINCIPAL_NAME);
        assertThat(appUser.getEmail()).isEqualTo(SAML_EMAIL);
        assertThat(appUser.getCommonName()).isEqualTo(SAML_COMMON_NAME);
        assertThat(appUser.getPersonId()).isEqualTo(SISU_PERSON_ID);
        assertThat(appUser.getStudentNumber().get()).isEqualTo(SAML_STUDENT_NUMBER_FINAL);
        assertThat(appUser.getPreferredLanguage()).isEqualTo(SAML_PREFERRED_LANGUAGE);
        assertThat(appUser.getEmployeeNumber().isPresent()).isFalse();
        assertThat(appUser.getAuthorities()).hasSize(1);

        GrantedAuthority grantedAuthority = Iterables.getOnlyElement(appUser.getAuthorities());
        assertThat(grantedAuthority.getAuthority()).isEqualTo(AppUser.Role.STUDENT.name());
    }

    @Test
    public void thatStudentAppUserWithMultipleUniversityStudentIDsReturnedWithUHStudentNumber() {
        SAMLCredential credential = samlStudentWithMultipleUniversityStudentIdsCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);
        assertThat(appUser.getStudentNumber().get()).isEqualTo(SAML_STUDENT_NUMBER_FINAL);
    }

    @Test
    public void thatStudentAppUserWithoutValidStudentIDIsReturnedWithoutStudentNumber() {
        SAMLCredential credential = samlNoValidStudentIDCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);
        assertThat(appUser.getStudentNumber().isPresent()).isFalse();
    }

    @Test
    public void thatTeacherAppUserIsReturned() {
        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertThat(appUser.getUsername()).isEqualTo(SAML_PRINCIPAL_NAME);
        assertThat(appUser.getEmail()).isEqualTo(SAML_EMAIL);
        assertThat(appUser.getCommonName()).isEqualTo(SAML_COMMON_NAME);
        assertThat(appUser.getPersonId()).isEqualTo(SISU_PERSON_ID);
        assertThat(appUser.getEmployeeNumber().get()).isEqualTo(SAML_EMPLOYEE_NUMBER);
        assertThat(appUser.getPreferredLanguage()).isEqualTo(SAML_PREFERRED_LANGUAGE);
        assertThat(appUser.getStudentNumber().isPresent()).isFalse();
        assertThat(appUser.getAuthorities()).hasSize(1);

        GrantedAuthority grantedAuthority = Iterables.getOnlyElement(appUser.getAuthorities());
        assertThat(grantedAuthority.getAuthority()).isEqualTo(AppUser.Role.TEACHER.name());
    }

    @Test
    public void thatHybridAppUserIsReturned() {
        SAMLCredential credential = samlHybridCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertThat(appUser.getEmployeeNumber().get()).isEqualTo(SAML_EMPLOYEE_NUMBER);
        assertThat(appUser.getStudentNumber().get()).isEqualTo(SAML_STUDENT_NUMBER_FINAL);
        assertThat(appUser.getAuthorities()).hasSize(2);
        assertThat(appUser.getAuthorities()).extracting("authority").contains(AppUser.Role.TEACHER.name());
        assertThat(appUser.getAuthorities()).extracting("authority").contains(AppUser.Role.STUDENT.name());
    }

    @Test
    public void thatUserIsNotAdmin() {
        when(userService.isAdmin(SAML_PRINCIPAL_NAME)).thenReturn(false);

        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertThat(appUser.hasRole(AppUser.Role.ADMIN)).isFalse();
    }

    @Test
    public void thatUserIsAdmin() {
        when(userService.isAdmin(SAML_PRINCIPAL_NAME)).thenReturn(true);

        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertThat(appUser.hasRole(AppUser.Role.ADMIN)).isTrue();
    }

    private SAMLCredential samlStudentCredential() {
        SAMLCredential credential = samlCommonCredential();

        when(credential.getAttributeAsStringArray(SAML_PERSONAL_UNIQUE_CODE_FIELD)).thenReturn(new String[] {SAML_STUDENT_NUMBER});
        return credential;
    }

    private SAMLCredential samlTeacherCredential() {
        SAMLCredential credential = samlCommonCredential();
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.3")).thenReturn(SAML_EMPLOYEE_NUMBER);
        return credential;
    }

    private SAMLCredential samlHybridCredential() {
        SAMLCredential credential = samlCommonCredential();
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.3")).thenReturn(SAML_EMPLOYEE_NUMBER);
        when(credential.getAttributeAsStringArray(SAML_PERSONAL_UNIQUE_CODE_FIELD)).thenReturn(new String[] {SAML_STUDENT_NUMBER});
        return credential;
    }

    private SAMLCredential samlCommonCredential() {
        SAMLCredential credential = mock(SAMLCredential.class);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.5923.1.1.1.6")).thenReturn(SAML_PRINCIPAL_NAME);
        when(credential.getAttributeAsString("urn:oid:0.9.2342.19200300.100.1.3")).thenReturn(SAML_EMAIL);
        when(credential.getAttributeAsString("urn:oid:2.5.4.3")).thenReturn(SAML_COMMON_NAME);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.18869.1.1.1.48")).thenReturn(SISU_PERSON_ID);
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.39")).thenReturn(SAML_PREFERRED_LANGUAGE);
        return credential;
    }

    private SAMLCredential samlStudentWithMultipleUniversityStudentIdsCredential() {
        SAMLCredential credential = samlCommonCredential();

        when(credential.getAttributeAsStringArray(SAML_PERSONAL_UNIQUE_CODE_FIELD))
            .thenReturn(new String[] {SAML_OTHER_UNIVERSITY_STUDENT_NUMBER, SAML_STUDENT_NUMBER});
        return credential;
    }

    private SAMLCredential samlNoValidStudentIDCredential() {
        SAMLCredential credential = samlCommonCredential();

        when(credential.getAttributeAsStringArray(SAML_PERSONAL_UNIQUE_CODE_FIELD))
            .thenReturn(new String[] {SAML_EMPLOYEE_PERSONAL_UNIQUE_CODE, SAML_OTHER_UNIVERSITY_STUDENT_NUMBER});
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.3")).thenReturn(SAML_EMPLOYEE_NUMBER);
        return credential;
    }

}
