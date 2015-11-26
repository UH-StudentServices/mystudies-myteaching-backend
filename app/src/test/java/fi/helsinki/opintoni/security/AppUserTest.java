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

import static org.junit.Assert.*;

public class AppUserTest {

    @Test(expected = BadCredentialsException.class)
    public void thatAppUserWithoutTeacherNorStudentNumberCannotBeCreated() {
        new AppUser.AppUserBuilder().build();
    }

    @Test
    public void thatPreferredLanguageIsSet() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .studentNumber("123")
            .preferredLanguage("fi")
            .build();
        assertEquals("fi", appUser.getPreferredLanguage());
    }

    @Test
    public void thatDefaultPreferredLanguageIsSet() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .studentNumber("123")
            .build();
        assertEquals("en", appUser.getPreferredLanguage());
    }

    @Test
    public void thatAdminRoleIsNotAddedByDefault() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .studentNumber("123")
            .build();

        assertFalse(isAdmin(appUser));
    }

    @Test
    public void thatAdminRoleIsAdded() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .studentNumber("123")
            .role(AppUser.Role.ADMIN)
            .build();

        assertTrue(isAdmin(appUser));
    }

    private boolean isAdmin(AppUser appUser) {
        return appUser.getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals(AppUser.Role.ADMIN.name()));
    }

}
