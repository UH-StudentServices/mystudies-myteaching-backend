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

import fi.helsinki.opintoni.security.HttpAuthenticationEntryPoint;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profile({
    Constants.SPRING_PROFILE_QA,
    Constants.SPRING_PROFILE_PRODUCTION,
    Constants.SPRING_PROFILE_LOCAL_SHIBBO
})
public class SAMLSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SAMLAuthenticationProvider samlAuthenticationProvider;

    @Autowired
    private FilterChainProxy loginTargetFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable();

        http
            .addFilterAfter(loginTargetFilter, BasicAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint);

        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/saml/**").permitAll()
            .antMatchers("/redirect").permitAll()
            .antMatchers("/api/public/v1/**").permitAll()
            .antMatchers("/api/public/v2/**").permitAll()
            .antMatchers("/api/admin/**").access(Constants.ADMIN_ROLE_REQUIRED)
            .antMatchers("/metrics/metrics/*").access(securityUtils.getWhitelistedIpAccess())
            .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(samlAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(RestConstants.PUBLIC_API_V1 + "/images/**");
    }
}
