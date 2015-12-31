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
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("localUserDetailsService")
@Profile({
    Constants.SPRING_PROFILE_TEST,
    Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT,
    Constants.SPRING_PROFILE_DEVELOPMENT
})
public class HardCodedUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final String USERNAME_STUDENT = "opiskelija";
    private static final String USERNAME_TEACHER = "opettaja";
    private static final String USERNAME_HYBDID = "hybriduser";
    private static final String USERNAME_TEST_STUDENT = "teststudent";
    private static final String USERNAME_TEST_TEACHER = "testteacher";
    private static final String USERNAME_TEST_HYBRID = "testhybriduser";

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        String lowercaseLogin = login.toLowerCase();

        if (USERNAME_STUDENT.equals(lowercaseLogin)) {
            return createStudent();
        } else if (USERNAME_TEACHER.equals(lowercaseLogin)) {
            return createTeacher();
        } else if (USERNAME_HYBDID.equals(lowercaseLogin)) {
            return createHybridUser();
        } else if (USERNAME_TEST_STUDENT.equals(lowercaseLogin)) {
            return createTestStudent();
        } else if (USERNAME_TEST_TEACHER.equals(lowercaseLogin)) {
            return createTestTeacher();
        } else if (USERNAME_TEST_HYBRID.equals(lowercaseLogin)) {
            return createTestHybridUser();
        }

        throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found.");
    }

    private UserDetails createTeacher() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("opettaja@helsinki.fi")
            .eduPersonAffiliation(SAMLEduPersonAffiliation.FACULTY)
            .email("opettaja@mail.helsinki.fi")
            .commonName("Olli Opettaja")
            .teacherNumber("010540")
            .oodiPersonId("1000")
            .role(AppUser.Role.ADMIN)
            .build();
    }

    private UserDetails createStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("opiskelija@helsinki.fi")
            .eduPersonAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .email("opiskelija@mail.helsinki.fi")
            .commonName("Olli Opiskelija")
            .studentNumber("010189791")
            .oodiPersonId("1001")
            .build();
    }

    private UserDetails createHybridUser() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("hybriduser@helsinki.fi")
            .eduPersonAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .email("hybriduser@mail.helsinki.fi")
            .commonName("Hybrid User")
            .teacherNumber("010540")
            .studentNumber("010189791")
            .oodiPersonId("1002")
            .build();
    }

    private UserDetails createTestTeacher() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testteachert@helsinki.fi")
            .email("testteachert@mail.helsinki.fi")
            .commonName("Test Teacher")
            .teacherNumber("010540")
            .oodiPersonId("1003")
            .build();
    }

    private UserDetails createTestStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("teststudent@helsinki.fi")
            .email("teststudent@mail.helsinki.fi")
            .commonName("Test Student")
            .studentNumber("010189791")
            .oodiPersonId("1004")
            .build();
    }

    private UserDetails createTestHybridUser() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testhybriduser@helsinki.fi")
            .email("testhybriduser@mail.helsinki.fi")
            .commonName("Test Hybrid User")
            .teacherNumber("010540")
            .studentNumber("010189791")
            .oodiPersonId("1005")
            .build();
    }
}
