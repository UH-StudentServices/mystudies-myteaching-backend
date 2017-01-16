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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestSecurityContext {

    private static String STUDENT_USERNAME = "opiskelija";
    private static String TEACHER_USERNAME = "opettaja";
    private static String PASSWORD = "password";
    private static String HYBRID_USER_USERNAME = "hybriduser";
    private static String TEACHER_WITHOUT_PORTFOLIO_USERNAME = "testteacher";

    public static SecurityContext studentSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(STUDENT_USERNAME, PASSWORD));
        return securityContext;
    }

    public static SecurityContext teacherSecurityContext() {
        return teacherSecurityContext(TEACHER_USERNAME, PASSWORD);
    }

    public static SecurityContext teacherWithoutPortfolioSecurityContext() {
        return teacherSecurityContext(TEACHER_WITHOUT_PORTFOLIO_USERNAME, PASSWORD);
    }

    public static SecurityContext teacherSecurityContext(String username, String password) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        return securityContext;
    }

    public static SecurityContext hybridUserSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(HYBRID_USER_USERNAME, PASSWORD));
        return securityContext;
    }

}
