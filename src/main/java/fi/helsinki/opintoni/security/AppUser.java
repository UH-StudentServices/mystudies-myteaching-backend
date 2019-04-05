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

import com.google.common.collect.Sets;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

public final class AppUser extends User {

    public enum Role {
        TEACHER, STUDENT, ADMIN
    }

    private final String email;
    private final String commonName;
    private final String eduPersonPrincipalName;
    private final List<SAMLEduPersonAffiliation> eduPersonAffiliations;
    private final SAMLEduPersonAffiliation eduPersonPrimaryAffiliation;
    private final String oodiPersonId;
    private final String preferredLanguage;
    private final Optional<String> teacherFacultyCode;
    private final Optional<String> studentNumber;
    private final Optional<String> employeeNumber;
    private final Collection<GrantedAuthority> authorities;

    private AppUser(AppUserBuilder builder) {

        super(builder.eduPersonPrincipalName, "password", builder.authorities);

        this.eduPersonPrincipalName = builder.eduPersonPrincipalName;
        this.eduPersonAffiliations = builder.eduPersonAffiliations;
        this.eduPersonPrimaryAffiliation = builder.eduPersonPrimaryAffiliation;
        this.email = builder.email;
        this.commonName = builder.commonName;
        this.studentNumber = builder.studentNumber;
        this.oodiPersonId = builder.oodiPersonId;
        this.employeeNumber = builder.employeeNumber;
        this.authorities = builder.authorities;
        this.teacherFacultyCode = builder.teacherFacultyCode;
        this.preferredLanguage = builder.preferredLanguage;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getEmail() {
        return email;
    }

    public Optional<String> getStudentNumber() {
        return studentNumber;
    }

    public Optional<String> getEmployeeNumber() {
        return employeeNumber;
    }

    public String getOodiPersonId() {
        return oodiPersonId;
    }

    public String getEduPersonPrincipalName() {
        return eduPersonPrincipalName;
    }

    public Optional<String> getTeacherFacultyCode() {
        return teacherFacultyCode;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public List<SAMLEduPersonAffiliation> getEduPersonAffiliations() {
        return eduPersonAffiliations;
    }

    public SAMLEduPersonAffiliation getEduPersonPrimaryAffiliation() {
        return eduPersonPrimaryAffiliation;
    }

    public boolean hasRole(Role role) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role.name()));
    }

    public boolean isTeacher() {
        return hasRole(Role.TEACHER);
    }

    public boolean isStudent() {
        return hasRole(Role.STUDENT);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("email", email)
            .append("eduPersonPrincipalName", eduPersonPrincipalName)
            .append("eduPersonAffiliation", eduPersonAffiliations.toString())
            .append("eduPersonPrimaryAffiliation", eduPersonPrimaryAffiliation.getValue())
            .append("commonName", commonName)
            .append("studentNumber", studentNumber)
            .append("employeeNumber", employeeNumber)
            .append("oodiPersonId", oodiPersonId)
            .toString();
    }

    public static class AppUserBuilder {

        private String eduPersonPrincipalName;
        private List<SAMLEduPersonAffiliation> eduPersonAffiliations;
        private SAMLEduPersonAffiliation eduPersonPrimaryAffiliation;
        private String email;
        private String commonName;
        private String oodiPersonId;
        private String preferredLanguage;
        private Optional<String> teacherFacultyCode = Optional.empty();
        private Optional<String> studentNumber = Optional.empty();
        private Optional<String> employeeNumber = Optional.empty();
        private Collection<GrantedAuthority> authorities = Collections.emptyList();
        private Set<Role> roles = Sets.newHashSet();

        public AppUserBuilder eduPersonPrincipalName(String eduPersonPrincipalName) {
            this.eduPersonPrincipalName = eduPersonPrincipalName;
            return this;
        }

        public AppUserBuilder eduPersonAffiliations(List<SAMLEduPersonAffiliation> eduPersonAffiliations) {
            this.eduPersonAffiliations = eduPersonAffiliations;
            return this;
        }

        public AppUserBuilder eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation eduPersonPrimaryAffiliation) {
            this.eduPersonPrimaryAffiliation = eduPersonPrimaryAffiliation;
            return this;
        }

        public AppUserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AppUserBuilder commonName(String commonName) {
            this.commonName = commonName;
            return this;
        }

        public AppUserBuilder oodiPersonId(String oodiPersonId) {
            this.oodiPersonId = oodiPersonId;
            return this;
        }

        public AppUserBuilder studentNumber(String studentNumber) {
            this.studentNumber = Optional.ofNullable(studentNumber);
            return this;
        }

        public AppUserBuilder employeeNumber(String employeeNumber) {
            this.employeeNumber = Optional.ofNullable(employeeNumber);
            return this;
        }

        public AppUserBuilder teacherFacultyCode(String teacherFacultyCode) {
            this.teacherFacultyCode = Optional.ofNullable(teacherFacultyCode);
            return this;
        }

        public AppUserBuilder preferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        public AppUserBuilder role(Role role) {
            roles.add(role);
            return this;
        }

        public AppUser build() {
            if (!employeeNumber.isPresent() && !studentNumber.isPresent()) {
                throw new BadCredentialsException("User does not have teacher nor student number");
            }

            if (eduPersonPrincipalName == null) {
                throw new BadCredentialsException("User does not have eduPersonPrincipalName");
            }

            if (eduPersonAffiliations == null || eduPersonAffiliations.isEmpty()) {
                throw new BadCredentialsException(
                    String.format("User (employeeNumber = %1$s, studentNumber = %2$s) does not have any eduPersonAffiliations",
                        employeeNumber.orElse(""), studentNumber.orElse("")));
            }

            authorities = getAuthorities();
            return new AppUser(this);
        }

        private Set<GrantedAuthority> getAuthorities() {
            Set<GrantedAuthority> authorities = new HashSet<>();

            if (studentNumber.isPresent()) {
                authorities.add(new SimpleGrantedAuthority(Role.STUDENT.name()));
            }

            if (employeeNumber.isPresent()) {
                authorities.add(new SimpleGrantedAuthority(Role.TEACHER.name()));
            }

            if (roles.contains(Role.ADMIN)) {
                authorities.add(new SimpleGrantedAuthority(Role.ADMIN.name()));
            }

            return authorities;
        }
    }
}
