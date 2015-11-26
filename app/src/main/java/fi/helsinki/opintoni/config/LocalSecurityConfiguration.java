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
import fi.helsinki.opintoni.security.CustomAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.HttpAuthenticationEntryPoint;
import fi.helsinki.opintoni.security.LocalLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
public class LocalSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("localUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomAuthenticationSuccessHandler authSuccessHandler;
    @Autowired
    private AuthFailureHandler authFailureHandler;
    @Autowired
    private LocalLogoutSuccessHandler localLogoutSuccessHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable();

        http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint);

        http
            .formLogin()
            .permitAll()
            .loginPage("/login")
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

        http
            .sessionManagement()
            .maximumSessions(1);

        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/redirect").permitAll()
            .antMatchers("/api/public/v1/**").permitAll()
            .antMatchers("/api/private/v1/admin/*").hasIpAddress("127.0.0.1")
            .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
            .anyRequest().authenticated();
    }
}
