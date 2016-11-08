package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.security.AuthFailureHandler;
import fi.helsinki.opintoni.security.CustomAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.HttpAuthenticationEntryPoint;
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
    Constants.SPRING_PROFILE_DEMO
})
public class DemoSecurityConfiguration extends WebSecurityConfigurerAdapter implements NonfederatedSecurityConfiguration {
    @Autowired
    @Qualifier("demoUserDetailsService")
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
    public void registerUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(RestConstants.PUBLIC_API_V1 + "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        useNonfederatedSecurityConfiguration(http, authenticationEntryPoint,
            authSuccessHandler, authFailureHandler, localLogoutSuccessHandler);
    }
}
