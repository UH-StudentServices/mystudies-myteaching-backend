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

import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.util.AuditLogger;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profile({
    Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT,
    Constants.SPRING_PROFILE_QA
})
public class PreAuthenticationSecurityConfiguration extends WebSecurityConfigurerAdapter implements NonfederatedSecurityConfiguration {

    private static final int MAX_CONCURRENT_SESSIONS = 1;

    @Autowired
    private UserService userService;
    @Autowired
    private Environment environment;
    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    public void registerUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider =  new PreAuthenticatedAuthenticationProvider();

        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedUserDetailsService());

        auth.authenticationProvider(preAuthenticatedAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        PreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter =
            new PreAuthenticatedProcessingFilter(userService, environment, auditLogger);
        preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());

        http
            .sessionManagement()
            .maximumSessions(MAX_CONCURRENT_SESSIONS);

        http
            .csrf()
            .disable();

        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/redirect").permitAll()
            .antMatchers("/api/public/v1/**").permitAll()
            .antMatchers("/api/public/v2/**").permitAll()
            .antMatchers("/api/private/v1/admin/*").hasIpAddress("127.0.0.1")
            .antMatchers("/api/admin/**").access(Constants.ADMIN_ROLE_REQUIRED)
            .anyRequest().authenticated()
            .and().addFilterBefore(preAuthenticatedProcessingFilter, LogoutFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(RestConstants.PUBLIC_API_V1 + "/images/**");
    }
}
