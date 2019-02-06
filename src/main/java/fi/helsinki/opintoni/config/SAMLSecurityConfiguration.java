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

import fi.helsinki.opintoni.security.CustomAuthenticationFailureHandler;
import fi.helsinki.opintoni.security.FederatedAuthenticationSuccessHandler;
import fi.helsinki.opintoni.security.SAMLLogoutSuccessHandler;
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
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderLB;
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
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Configuration
@Profile({
    Constants.SPRING_PROFILE_QA,
    Constants.SPRING_PROFILE_PRODUCTION,
    Constants.SPRING_PROFILE_LOCAL_SHIBBO
})
public class SAMLSecurityConfiguration {

    private static final int ONE_WEEK_IN_SECONDS = 604800;

    @Value("${hostUrl}")
    private String hostUrl;

    @Value("${saml.teacher.alias}")
    private String samlTeacherAlias;

    @Value("${saml.student.alias}")
    private String samlStudentAlias;

    @Value("${saml.keystorePassword}")
    private String samlKeystorePassword;

    @Value("${saml.keystoreLocation}")
    private String samlKeystoreLocation;

    @Value("${saml.idp.metadataUrl}")
    private String samlIdpMetadataUrl;

    @Autowired
    private SAMLLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private FederatedAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private SingleLogoutProfile singleLogoutProfile;

    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean("contextProvider")
    @Profile({
        Constants.SPRING_PROFILE_QA,
        Constants.SPRING_PROFILE_PRODUCTION
    })
    public SAMLContextProviderLB contextProvider() throws URISyntaxException {
        SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
        URI uri = new URI(hostUrl);
        contextProvider.setScheme(uri.getScheme());
        contextProvider.setServerPort(uri.getPort());
        contextProvider.setServerName(uri.getHost());
        contextProvider.setContextPath("");
        return contextProvider;
    }

    @Bean("contextProvider")
    @Profile({
        Constants.SPRING_PROFILE_LOCAL_SHIBBO
    })
    public SAMLContextProvider localShibbocontextProvider() throws URISyntaxException {
        SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
        URI uri = new URI(hostUrl);
        contextProvider.setScheme(uri.getScheme());
        contextProvider.setServerPort(uri.getPort());
        contextProvider.setServerName(uri.getHost());
        contextProvider.setContextPath("");
        contextProvider.setIncludeServerPortInRequestURL(true);
        return contextProvider;
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
    public SAMLAuthenticationProvider samlAuthenticationProvider(@Autowired SAMLUserDetailsService samlUserDetailsService) {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return samlAuthenticationProvider;
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
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    @Bean
    @Qualifier("metadata")
    @Profile({
        Constants.SPRING_PROFILE_QA,
        Constants.SPRING_PROFILE_PRODUCTION
    })
    public CachingMetadataManager metadata(@Autowired ParserPool parserPool,
                                           @Autowired ExtendedMetadataDelegate idpMetadata) throws Exception {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(idpMetadata);
        providers.add(spMetadata(samlTeacherAlias, true, parserPool));
        providers.add(spMetadata(samlStudentAlias, true, parserPool));
        return new CachingMetadataManager(providers);
    }

    @Bean
    @Qualifier("metadata")
    @Profile({
        Constants.SPRING_PROFILE_LOCAL_SHIBBO
    })
    public CachingMetadataManager metadataLocalShibbo(@Autowired ParserPool parserPool,
                                                      @Autowired ExtendedMetadataDelegate idpMetadata) throws Exception {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(idpMetadata);
        providers.add(spMetadata(samlTeacherAlias, false, parserPool));
        providers.add(spMetadata(samlStudentAlias, false, parserPool));
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
    public SAMLProcessingFilter samlWebSSOProcessingFilter(@Autowired AuthenticationManager authenticationManager) throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager);
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter(@Autowired SAMLContextProvider contextProvider,
                                             @Autowired SAMLLogger samlLogger,
                                             @Autowired SecurityContextLogoutHandler logoutHandler) {
        final SAMLLogoutFilter filter = new SAMLLogoutFilter(logoutSuccessHandler,
            new LogoutHandler[]{logoutHandler},
            new LogoutHandler[]{logoutHandler});
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
    public HTTPPostBinding httpPostBinding(@Autowired ParserPool parserPool) {
        return new HTTPPostBinding(parserPool, velocityEngine());
    }

    @Bean
    HTTPRedirectDeflateBinding httpRedirectBinding(@Autowired ParserPool parserPool) {
        return new HTTPRedirectDeflateBinding(parserPool);
    }

    @Bean
    public SAMLProcessorImpl processor(@Autowired HTTPPostBinding httpPostBinding, @Autowired HTTPRedirectDeflateBinding httpRedirectBinding) {
        Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpPostBinding);
        bindings.add(httpRedirectBinding);
        return new SAMLProcessorImpl(bindings);
    }

    @Bean
    public FilterChainProxy samlFilter(@Autowired SAMLLogoutFilter samlLogoutFilter,
                                       @Autowired SAMLEntryPoint samlEntryPoint,
                                       @Autowired SAMLLogoutProcessingFilter samlLogoutProcessingFilter,
                                       @Autowired SAMLProcessingFilter samlWebSSOProcessingFilter) throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
            samlEntryPoint));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
            samlLogoutFilter));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
            samlLogoutProcessingFilter));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
            samlWebSSOProcessingFilter));
        return new FilterChainProxy(chains);
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
    @Qualifier("idp-ssocircle")
    public ExtendedMetadataDelegate idpMetadata(@Autowired ParserPool parserPool) throws MetadataProviderException {
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

    private ExtendedMetadata idpExtendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setRequireLogoutResponseSigned(true);
        return extendedMetadata;
    }

    private FilesystemResource getSpMetadata(String alias) throws Exception {
        return new FilesystemResource(appConfiguration.get("saml." + alias + ".metadataLocation"));
    }

    private ExtendedMetadata spExtendedMetadata(String alias) {
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

    private ExtendedMetadataDelegate spMetadata(String alias, boolean requireSignature, ParserPool parserPool) throws Exception {
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

}
