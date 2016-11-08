package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.security.AuthFailureHandler;
import fi.helsinki.opintoni.security.CustomAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.HttpAuthenticationEntryPoint;
import fi.helsinki.opintoni.security.LocalLogoutSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface NonfederatedSecurityConfiguration {




    default void useNonfederatedSecurityConfiguration(HttpSecurity http,
                                                      HttpAuthenticationEntryPoint authenticationEntryPoint,
                                                      CustomAuthenticationSuccessHandler authSuccessHandler,
                                                      AuthFailureHandler authFailureHandler,
                                                      LocalLogoutSuccessHandler localLogoutSuccessHandler) throws Exception {
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
            .antMatchers("/api/private/v1/admin/*").hasIpAddress("127.0.0.1")
            .antMatchers("/api/admin/**").access(Constants.ADMIN_ROLE_REQUIRED)
            .anyRequest().authenticated();
    }
}
