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

import fi.helsinki.opintoni.security.*;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.FilesystemResource;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.log.SAMLLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;

import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.File;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profile({
    Constants.SPRING_PROFILE_QA,
    Constants.SPRING_PROFILE_PRODUCTION,
    Constants.SPRING_PROFILE_LOCAL_SHIBBO
})
public class SAMLSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    private static final int ONE_WEEK_IN_SECONDS = 604800;

    @Value("${saml.idp.metadataUrl}")
    private String samlIdpMetadataUrl;

    @Value("${saml.teacher.alias}")
    private String samlTeacherAlias;

    @Value("${saml.student.alias}")
    private String samlStudentAlias;

    @Value("${saml.keystoreLocation}")
    private String samlKeystoreLocation;

    @Value("${saml.keystorePassword}")
    private String samlKeystorePassword;

    @Autowired
    private ParserPool  parserPool;

    @Autowired
    private SAMLUserDetailsService samlUserDetailsService;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private FederatedAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SAMLLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private SingleLogoutProfile singleLogoutProfile;

    @Autowired
    private SAMLLogger samlLogger;

    @Autowired
    private SAMLLogoutFilter samlFilter;

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return samlAuthenticationProvider;
    }

    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public SAMLLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        WebSSOProfileConsumerImpl webSSOProfileConsumer = new WebSSOProfileConsumerImpl();
        webSSOProfileConsumer.setMaxAuthenticationAge(ONE_WEEK_IN_SECONDS);
        return webSSOProfileConsumer;
    }

    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    @Bean
    public SingleLogoutProfile logoutProfile() {
        return new SingleLogoutProfileImpl();
    }

    @Bean
    public KeyManager keyManager() {
        FileSystemResource fileSystemResource = new FileSystemResource(
            new File(samlKeystoreLocation));
        Map<String, String> passwords = new HashMap<>();
        passwords.put(getKeystoreAlias(samlTeacherAlias), samlKeystorePassword);
        passwords.put(getKeystoreAlias(samlStudentAlias), samlKeystorePassword);
        return new JKSKeyManager(fileSystemResource, samlKeystorePassword, passwords, samlTeacherAlias);
    }

    @Bean
    public TLSProtocolConfigurer tlsProtocolConfigurer() {
        return new TLSProtocolConfigurer();
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }

    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    @Bean
    @Qualifier("idp-ssocircle")
    public ExtendedMetadataDelegate idpMetadata() throws MetadataProviderException {
        HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(
            new Timer("idpMetadataRefreshTimer"),
            new HttpClient(),
            samlIdpMetadataUrl);
        httpMetadataProvider.setParserPool(parserPool);

        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(httpMetadataProvider,
            idpExtendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);

        return extendedMetadataDelegate;
    }

    public ExtendedMetadata idpExtendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setRequireLogoutResponseSigned(true);
        return extendedMetadata;
    }

    private ExtendedMetadataDelegate spMetadata(String alias, boolean requireSignature) throws Exception {
        ResourceBackedMetadataProvider resourceBackedMetadataProvider = new ResourceBackedMetadataProvider(
            new Timer(),
            getSpMetadata(alias));
        resourceBackedMetadataProvider.setParserPool(parserPool);
        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(resourceBackedMetadataProvider,
            spExtendedMetadata(alias));
        extendedMetadataDelegate.setMetadataTrustCheck(true);
        extendedMetadataDelegate.setMetadataRequireSignature(requireSignature);
        return extendedMetadataDelegate;
    }

    private FilesystemResource getSpMetadata(String alias) throws Exception {
        return new FilesystemResource(appConfiguration.get("saml." + alias + ".metadataLocation"));
    }

    public ExtendedMetadata spExtendedMetadata(String alias) {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setLocal(true);
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setAlias(alias);
        extendedMetadata.setSigningKey(getKeystoreAlias(alias));
        extendedMetadata.setEncryptionKey(getKeystoreAlias(alias));
        return extendedMetadata;
    }

    private String getKeystoreAlias(String alias) {
        String keystoreAlias = appConfiguration.get("saml." + alias + ".domain");
        if (keystoreAlias == null) {
            throw new IllegalArgumentException("saml." + alias + ".domain must be configured");
        }
        return keystoreAlias;
    }

    @Bean
    @Qualifier("metadata")
    @Profile({
        Constants.SPRING_PROFILE_QA,
        Constants.SPRING_PROFILE_PRODUCTION
    })
    public CachingMetadataManager metadata() throws Exception {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(idpMetadata());
        providers.add(spMetadata(samlTeacherAlias, true));
        providers.add(spMetadata(samlStudentAlias, true));
        return new CachingMetadataManager(providers);
    }

    @Bean
    @Qualifier("metadata")
    @Profile({
        Constants.SPRING_PROFILE_LOCAL_SHIBBO
    })
    public CachingMetadataManager metadataLocalShibbo() throws Exception {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(idpMetadata());
        providers.add(spMetadata(samlTeacherAlias, false));
        providers.add(spMetadata(samlStudentAlias, false));
        return new CachingMetadataManager(providers);
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler =
            new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setUseForward(true);
        failureHandler.setDefaultFailureUrl("/error");
        return failureHandler;
    }

    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(false);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter(@Autowired SAMLContextProvider contextProvider) {
        final SAMLLogoutFilter filter =  new SAMLLogoutFilter(logoutSuccessHandler,
            new LogoutHandler[]{logoutHandler()},
            new LogoutHandler[]{logoutHandler()});
        filter.setContextProvider(contextProvider);
        filter.setProfile(singleLogoutProfile);
        filter.setSamlLogger(samlLogger);
        return filter;
    }

    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(logoutSuccessHandler,
            logoutHandler());
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool, velocityEngine());
    }

    @Bean
    HTTPRedirectDeflateBinding httpRedirectBinding() {
        return new HTTPRedirectDeflateBinding(parserPool);
    }

    @Bean
    public SAMLProcessorImpl processor() {
        Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpPostBinding());
        bindings.add(httpRedirectBinding());
        return new SAMLProcessorImpl(bindings);
    }

    @Bean
    public FilterChainProxy samlFilter(@Autowired SAMLLogoutFilter samlLogoutFilter) throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
            samlEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
            samlLogoutFilter));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
            samlLogoutProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
            samlWebSSOProcessingFilter()));
        return new FilterChainProxy(chains);
    }

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
            .addFilterAfter(samlFilter, BasicAuthenticationFilter.class)
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
        auth.authenticationProvider(samlAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(RestConstants.PUBLIC_API_V1 + "/images/**");
    }
}
