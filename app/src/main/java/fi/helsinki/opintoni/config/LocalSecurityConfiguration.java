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

import fi.helsinki.opintoni.security.AuthFailureHandler;
import fi.helsinki.opintoni.security.BaseAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.HttpAuthenticationEntryPoint;
import fi.helsinki.opintoni.security.LocalAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.LocalLogoutSuccessHandler;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profile({
    Constants.SPRING_PROFILE_TEST,
    Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT,
    Constants.SPRING_PROFILE_DEVELOPMENT
})
public class LocalSecurityConfiguration extends WebSecurityConfigurerAdapter implements NonfederatedSecurityConfiguration {

    private static final int MAX_CONCURRENT_SESSIONS = 1;

    @Autowired
    @Qualifier("localUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private LocalAuthenticationSuccessHandler authSuccessHandler;
    @Autowired
    private AuthFailureHandler authFailureHandler;
    @Autowired
    private LocalLogoutSuccessHandler localLogoutSuccessHandler;

    @Autowired
    public void registerUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        useNonfederatedSecurityConfiguration(http, authenticationEntryPoint,
            authSuccessHandler, authFailureHandler, localLogoutSuccessHandler);

        http
            .sessionManagement()
            .maximumSessions(MAX_CONCURRENT_SESSIONS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(RestConstants.PUBLIC_API_V1 + "/images/**");
    }
}
