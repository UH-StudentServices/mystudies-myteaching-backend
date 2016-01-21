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
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SAMLUserDetailsServiceTest {

    private static final String SAML_EMAIL = "email";
    private static final String SAML_COMMON_NAME = "commonName";
    private static final String SAML_PRINCIPAL_NAME = "eduPersonPrincipalName";
    private static final String SAML_STUDENT_NUMBER = "urn:mace:terena" +
        ".org:schac:personalUniqueCode:int:studentID:helsinki.fi:011631484";
    private static final String SAML_STUDENT_NUMBER_FINAL = "011631484";
    private static final String SAML_TEACHER_NUMBER = "teacherNumber";
    private static final String OODI_PERSON_ID = "1440748";
    private static final String SAML_PREFERRED_LANGUAGE = "fi";
    private static final String[] SAML_EDU_PERSON_AFFILIATION_MULTIVALUE = {"member", "student"};
    private static final String[] SAML_EDU_PERSON_AFFILIATION_FACULTY = {"faculty"};
    private static final String[] SAML_EDU_PERSON_AFFILIATION_HYBRID = {"faculty", "student", "member"};

    private final UserService userService = mock(UserService.class);

    private SAMLUserDetailsService userDetailsService = new SAMLUserDetailsService(userService);

    @Test
    public void thatStudentAppUserIsReturned() {
        SAMLCredential credential = samlStudentCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertEquals(SAML_PRINCIPAL_NAME, appUser.getUsername());
        assertEquals(SAML_EMAIL, appUser.getEmail());
        assertEquals(SAML_COMMON_NAME, appUser.getCommonName());
        assertEquals(OODI_PERSON_ID, appUser.getOodiPersonId());
        assertEquals(SAML_STUDENT_NUMBER_FINAL, appUser.getStudentNumber().get());
        assertEquals(SAML_PREFERRED_LANGUAGE, appUser.getPreferredLanguage());
        assertTrue(appUser.getEduPersonAffiliations().contains(SAMLEduPersonAffiliation.STUDENT));
        assertEquals(SAMLEduPersonAffiliation.STUDENT, appUser.getEduPersonPrimaryAffiliation());
        assertFalse(appUser.getTeacherNumber().isPresent());
        assertEquals(1, appUser.getAuthorities().size());

        GrantedAuthority grantedAuthority = Iterables.getOnlyElement(appUser.getAuthorities());
        assertEquals(AppUser.Role.STUDENT.name(), grantedAuthority.getAuthority());
    }

    @Test
    public void thatTeacherAppUserIsReturned() {
        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertEquals(SAML_PRINCIPAL_NAME, appUser.getUsername());
        assertEquals(SAML_EMAIL, appUser.getEmail());
        assertEquals(SAML_COMMON_NAME, appUser.getCommonName());
        assertEquals(OODI_PERSON_ID, appUser.getOodiPersonId());
        assertEquals(SAML_TEACHER_NUMBER, appUser.getTeacherNumber().get());
        assertEquals(SAML_PREFERRED_LANGUAGE, appUser.getPreferredLanguage());
        assertTrue(appUser.getEduPersonAffiliations().contains(SAMLEduPersonAffiliation.FACULTY));
        assertEquals(SAMLEduPersonAffiliation.FACULTY, appUser.getEduPersonPrimaryAffiliation());
        assertFalse(appUser.getStudentNumber().isPresent());
        assertEquals(1, appUser.getAuthorities().size());

        GrantedAuthority grantedAuthority = Iterables.getOnlyElement(appUser.getAuthorities());
        assertEquals(AppUser.Role.TEACHER.name(), grantedAuthority.getAuthority());
    }

    @Test
    public void thatHybridAppUserIsReturned() {
        SAMLCredential credential = samlHybridCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertEquals(SAML_TEACHER_NUMBER, appUser.getTeacherNumber().get());
        assertEquals(SAML_STUDENT_NUMBER_FINAL, appUser.getStudentNumber().get());
        assertEquals(2, appUser.getAuthorities().size());
        assertThat(appUser.getAuthorities(),
            hasItem(Matchers.hasProperty("authority", equalTo(AppUser.Role.TEACHER.name()))));
        assertThat(appUser.getAuthorities(),
            hasItem(Matchers.hasProperty("authority", equalTo(AppUser.Role.STUDENT.name()))));
    }

    @Test
    public void thatUserIsNotAdmin() {
        when(userService.isAdmin(SAML_PRINCIPAL_NAME)).thenReturn(false);

        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertFalse(appUser.hasRole(AppUser.Role.ADMIN));
    }

    @Test
    public void thatUserIsAdmin() {
        when(userService.isAdmin(SAML_PRINCIPAL_NAME)).thenReturn(true);

        SAMLCredential credential = samlTeacherCredential();

        AppUser appUser = (AppUser) userDetailsService.loadUserBySAML(credential);

        assertTrue(appUser.hasRole(AppUser.Role.ADMIN));
    }

    private SAMLCredential samlStudentCredential() {
        SAMLCredential credential = samlCommonCredential();

        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.25178.1.2.14")).thenReturn(SAML_STUDENT_NUMBER);
        when(credential.getAttributeAsStringArray("urn:oid:1.3.6.1.4.1.5923.1.1.1.1")).thenReturn(SAML_EDU_PERSON_AFFILIATION_MULTIVALUE);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.5923.1.1.1.5")).thenReturn(SAMLEduPersonAffiliation.STUDENT.getValue());
        return credential;
    }

    private SAMLCredential samlTeacherCredential() {
        SAMLCredential credential = samlCommonCredential();
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.3")).thenReturn(SAML_TEACHER_NUMBER);
        when(credential.getAttributeAsStringArray("urn:oid:1.3.6.1.4.1.5923.1.1.1.1")).thenReturn(SAML_EDU_PERSON_AFFILIATION_FACULTY);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.5923.1.1.1.5")).thenReturn(SAMLEduPersonAffiliation.FACULTY.getValue());
        return credential;
    }

    private SAMLCredential samlHybridCredential() {
        SAMLCredential credential = samlCommonCredential();
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.3")).thenReturn(SAML_TEACHER_NUMBER);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.25178.1.2.14")).thenReturn(SAML_STUDENT_NUMBER);
        when(credential.getAttributeAsStringArray("urn:oid:1.3.6.1.4.1.5923.1.1.1.1")).thenReturn(SAML_EDU_PERSON_AFFILIATION_HYBRID);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.5923.1.1.1.5")).thenReturn(SAMLEduPersonAffiliation.FACULTY.getValue());
        return credential;
    }

    private SAMLCredential samlCommonCredential() {
        SAMLCredential credential = mock(SAMLCredential.class);
        when(credential.getAttributeAsString("urn:oid:1.3.6.1.4.1.5923.1.1.1.6")).thenReturn(SAML_PRINCIPAL_NAME);
        when(credential.getAttributeAsString("urn:oid:0.9.2342.19200300.100.1.3")).thenReturn(SAML_EMAIL);
        when(credential.getAttributeAsString("urn:oid:2.5.4.3")).thenReturn(SAML_COMMON_NAME);
        when(credential.getAttributeAsString("1.3.6.1.4.1.18869.1.1.1.32")).thenReturn(OODI_PERSON_ID);
        when(credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.39")).thenReturn(SAML_PREFERRED_LANGUAGE);
        return credential;
    }

}
