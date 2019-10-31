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

import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static fi.helsinki.opintoni.security.AppUser.Role;
import static fi.helsinki.opintoni.security.AppUser.Role.ADMIN;
import static fi.helsinki.opintoni.security.AppUser.Role.STUDENT;
import static fi.helsinki.opintoni.security.AppUser.Role.TEACHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AppUserTest {

    private static final String STUDENT_NUMBER = "123";
    private static final String EMPLOYEE_NUMBER = "321";
    private static final String EDU_PERSON_PRINCIPAL_NAME = "eduPersonPrincipalName";
    private static final String PERSON_ID = "1234";

    @Test
    public void thatAppUserWithoutTeacherNorStudentNumberCannotBeCreated() {
        failsWithMessage(new AppUser.AppUserBuilder()
                .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
                .personId(PERSON_ID),
            "User does not have teacher nor student number",
            "eduPersonPrincipalName=eduPersonPrincipalName");
    }

    @Test
    public void thatAppUserWithoutPersonIdCannotBeCreated() {
        failsWithMessage(new AppUser.AppUserBuilder()
                .studentNumber(STUDENT_NUMBER)
                .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME),
            "User does not have personId",
            "eduPersonPrincipalName=eduPersonPrincipalName");
    }

    @Test
    public void thatAppUserWithoutEppnNumberCannotBeCreated() {
        failsWithMessage(new AppUser.AppUserBuilder()
                .studentNumber(STUDENT_NUMBER)
                .personId(PERSON_ID),
            "User does not have eduPersonPrincipalName",
            "personId=1234");
    }

    private void failsWithMessage(AppUser.AppUserBuilder ub, String...expectedMessages) {
        try {
            ub.build();
            fail("Should have thrown an exception.");
        } catch (BadCredentialsException e) {
            for (String message : expectedMessages) {
                assertThat(e.getMessage()).contains(message);
            }
        }
    }

    @Test
    public void thatPreferredLanguageIsSet() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .studentNumber(STUDENT_NUMBER)
            .preferredLanguage("fi")
            .personId(PERSON_ID)
            .build();
        assertThat(appUser.getPreferredLanguage()).isEqualTo("fi");
    }

    @Test
    public void thatAdminRoleIsNotAddedByDefault() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .studentNumber(STUDENT_NUMBER)
            .personId(PERSON_ID)
            .build();

        assertThat(isInRole(appUser, ADMIN)).isFalse();
    }

    @Test
    public void thatAdminRoleIsAdded() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .studentNumber(STUDENT_NUMBER)
            .personId(PERSON_ID)
            .role(ADMIN)
            .build();

        assertThat(isInRole(appUser, ADMIN)).isTrue();
    }

    @Test
    public void thatUserHasStudentRole() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .personId(PERSON_ID)
            .studentNumber(STUDENT_NUMBER)
            .build();

        assertThat(isInRole(appUser, STUDENT)).isTrue();
    }

    @Test
    public void thatUserHasTeacherRole() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .personId(PERSON_ID)
            .employeeNumber(EMPLOYEE_NUMBER)
            .build();

        assertThat(isInRole(appUser, TEACHER)).isTrue();
    }

    @Test
    public void thatUserHasStudentAndTeacherRole() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME)
            .personId(PERSON_ID)
            .studentNumber(STUDENT_NUMBER)
            .employeeNumber(EMPLOYEE_NUMBER)
            .build();

        assertThat(isInRole(appUser, STUDENT)).isTrue();
        assertThat(isInRole(appUser, TEACHER)).isTrue();
    }

    private boolean isInRole(AppUser appUser, Role role) {
        return appUser.getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals(role.name()));
    }

}
