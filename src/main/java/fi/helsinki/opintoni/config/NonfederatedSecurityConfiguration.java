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

package fi.helsinki.opintoni.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public interface NonfederatedSecurityConfiguration {

    default void useNonfederatedSecurityConfiguration(HttpSecurity http,
                                                      AuthenticationEntryPoint authenticationEntryPoint,
                                                      AuthenticationSuccessHandler authSuccessHandler,
                                                      AuthenticationFailureHandler authFailureHandler,
                                                      LogoutSuccessHandler localLogoutSuccessHandler) throws Exception {
        http
            .csrf()
            .disable();

        http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint);

        http
            .formLogin()
            .permitAll()
            .loginProcessingUrl("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(authSuccessHandler)
            .failureHandler(authFailureHandler);

        http
            .logout()
            .logoutUrl("/logout")
            .permitAll()
            .logoutSuccessHandler(localLogoutSuccessHandler);

        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/redirect").permitAll()
            .antMatchers("/api/public/v1/**").permitAll()
            .antMatchers("/api/public/v2/**").permitAll()
            .antMatchers("/api/private/v1/admin/*").hasIpAddress("127.0.0.1")
            .antMatchers("/api/admin/**").access(Constants.ADMIN_ROLE_REQUIRED)
            .anyRequest().authenticated();
    }
}
