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
public class DevUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final String USERNAME_STUDENT = "opiskelija";
    private static final String USERNAME_MAG = "mag_simp";
    private static final String USERNAME_TEACHER = "opettaja";
    private static final String USERNAME_HYBDID = "hybriduser";
    private static final String USERNAME_TEST_STUDENT = "teststudent";
    private static final String USERNAME_TEST_TEACHER = "testteacher";
    private static final String USERNAME_TEST_HYBRID = "testhybriduser";
    private static final String USERNAME_TEST_OPEN_UNI_STUDENT = "testopenunistudent";
    private static final String USERNAME_TEST_NEW_STUDENT = "testnewstudent";
    public static final String STUDENT_NUMBER_TEST_OPEN_UNI_STUDENT = "010189792";
    public static final String STUDENT_NUMBER_TEST_NEW_STUDENT = "010189793";

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        String lowercaseLogin = login.toLowerCase();

        if (USERNAME_STUDENT.equals(lowercaseLogin)) {
            return createStudent();
        } else if (USERNAME_MAG.equals(lowercaseLogin)) {
            return createMag();
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
        } else if (USERNAME_TEST_OPEN_UNI_STUDENT.equals(lowercaseLogin)) {
            return createTestOpenUniStudent();
        } else if (USERNAME_TEST_NEW_STUDENT.equals(lowercaseLogin)) {
            return createTestNewStudent();
        }

        throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found.");
    }

    private UserDetails createTeacher() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("opettaja@helsinki.fi")
            .teacherFacultyCode("H30")
            .email("opettaja@mail.helsinki.fi")
            .commonName("Olli Opettaja")
            .employeeNumber("010540")
            .personId("1000")
            .role(AppUser.Role.ADMIN)
            .build();
    }

    private UserDetails createStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("opiskelija@helsinki.fi")
            .email("opiskelija@mail.helsinki.fi")
            .commonName("Olli Opiskelija")
            .studentNumber("010189791")
            .personId("1001")
            .build();
    }

    private UserDetails createMag() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("mag_simp@helsinki.fi")
            .email("opiskelija@mail.helsinki.fi")
            .commonName("Maggie Simpson")
            .studentNumber("011631484")
            .personId("80")
            .build();
    }

    private UserDetails createHybridUser() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("hybriduser@helsinki.fi")
            .teacherFacultyCode("H50")
            .email("hybriduser@mail.helsinki.fi")
            .commonName("Hybrid User")
            .employeeNumber("010540")
            .studentNumber("010189791")
            .personId("1002")
            .build();
    }

    private UserDetails createTestTeacher() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testteacher@helsinki.fi")
            .teacherFacultyCode("H40")
            .email("testteacher@mail.helsinki.fi")
            .commonName("Test Teacher")
            .employeeNumber("010540")
            .personId("1003")
            .build();
    }

    private UserDetails createTestStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("teststudent@helsinki.fi")
            .email("teststudent@mail.helsinki.fi")
            .commonName("Test Student")
            .studentNumber("010189791")
            .personId("1004")
            .build();
    }

    private UserDetails createTestHybridUser() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testhybriduser@helsinki.fi")
            .teacherFacultyCode("H10")
            .email("testhybriduser@mail.helsinki.fi")
            .commonName("Test Hybrid User")
            .employeeNumber("010540")
            .studentNumber("010189791")
            .personId("1005")
            .build();
    }

    private UserDetails createTestOpenUniStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testopenunistudent@helsinki.fi")
            .email("testopenunistudent@mail.helsinki.fi")
            .commonName("Test Open Uni Student")
            .studentNumber(STUDENT_NUMBER_TEST_OPEN_UNI_STUDENT)
            .personId("1006")
            .build();
    }

    private UserDetails createTestNewStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("testnewstudent@helsinki.fi")
            .email("testnewstudent@mail.helsinki.fi")
            .commonName("Test New Student")
            .studentNumber(STUDENT_NUMBER_TEST_NEW_STUDENT)
            .personId("1007")
            .build();
    }
}
