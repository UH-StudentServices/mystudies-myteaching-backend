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

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

@Component("demoUserDetailsService")
@Profile({
    Constants.SPRING_PROFILE_DEMO
})
public class DemoUserDetailsService implements UserDetailsService {
    private final ImmutableMap<String, Supplier<UserDetails>> USER_CONF = ImmutableMap.of(
        "e_jukola", this::newTeacher,
        "v_jukola", this::newStudent,
        "j_jukola", this::newHybrid,
        "k_rajama", this::newOpenUniStudent
    );

    @Override
    public UserDetails loadUserByUsername(String username) {
        String normalizedUsername = username.toLowerCase();

        if(USER_CONF.containsKey(normalizedUsername)) {
            return USER_CONF.get(normalizedUsername).get();
        } else {
            throw new UsernameNotFoundException("Unknown user: " + normalizedUsername);
        }
    }

    private UserDetails newTeacher() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("doo_8@helsinki.fi")
            .eduPersonAffiliations(singletonList(SAMLEduPersonAffiliation.FACULTY))
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.FACULTY)
            .teacherFacultyCode("H30")
            .email("doo_8@example.com")
            .commonName("Eero Jukola")
            .employeeNumber("007505")
            .oodiPersonId("109155648")
            .build();
    }

    private UserDetails newStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("doo_9@helsinki.fi")
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.MEMBER, SAMLEduPersonAffiliation.STUDENT))
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .email("doo_9@example.com")
            .commonName("Venla Jukola")
            .studentNumber("010096350")
            .oodiPersonId("109155672")
            .build();
    }

    private UserDetails newHybrid() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("doo_10@helsinki.fi")
            .eduPersonAffiliations(singletonList(SAMLEduPersonAffiliation.STUDENT))
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.FACULTY)
            .teacherFacultyCode("H50")
            .email("doo_10@example.com")
            .commonName("Juhani Jukola")
            .employeeNumber("007507")
            .studentNumber("010095623")
            .oodiPersonId("109155650")
            .build();
    }

    private UserDetails newOpenUniStudent() {
        return new AppUser.AppUserBuilder()
            .eduPersonPrincipalName("doo_11@helsinki.fi")
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.AFFILIATE))
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.AFFILIATE)
            .email("doo_11@example.com")
            .commonName("Kaisa Rajamäki")
            .studentNumber("010094899")
            .oodiPersonId("109155680")
            .build();
    }
}
