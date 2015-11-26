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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mbeans.JmxRemoteLifecycleListener;
import org.apache.catalina.valves.RemoteIpValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.embedded.*;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
@AutoConfigureAfter(CacheConfiguration.class)
public class WebConfigurer implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    @Inject
    private Environment env;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_TEST)
    public EmbeddedServletContainerFactory servletContainer() {
        if (env.acceptsProfiles(Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT)) {
            return createUndertow();
        } else {
            return createTomcat();
        }
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/errors/403.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/errors/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/errors/500.html");

            container.addErrorPages(error403Page, error404Page, error500Page);
        });
    }

    private EmbeddedServletContainerFactory createUndertow() {
        return new UndertowEmbeddedServletContainerFactory();
    }

    private EmbeddedServletContainerFactory createTomcat() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

        /*
         * JmxRemoteLifecycleListener needs to be set to fixed ports for JMX monitoring.
         * If not set, Java would select random ports and firewall would block RMI connections.
         */
        JmxRemoteLifecycleListener jmxRemoteLifecycleListener = new JmxRemoteLifecycleListener();
        jmxRemoteLifecycleListener.setRmiRegistryPortPlatform(5000);
        jmxRemoteLifecycleListener.setRmiServerPortPlatform(5001);
        tomcat.addContextLifecycleListeners(jmxRemoteLifecycleListener);

        RemoteIpValve remoteIpValve = new RemoteIpValve();
        remoteIpValve.setRemoteIpHeader("x-forwarded-for");
        remoteIpValve.setInternalProxies(appConfiguration.get("internalProxies"));
        tomcat.addContextValves(remoteIpValve);

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setProxyPort(appConfiguration.getInteger("http.connector.proxy_port"));
        connector.setPort(appConfiguration.getInteger("http.connector.app_port"));
        tomcat.addAdditionalTomcatConnectors(connector);

        return tomcat;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        EnumSet<DispatcherType> dispatcherTypes =
            EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        if (env.acceptsProfiles(
            Constants.SPRING_PROFILE_DEVELOPMENT,
            Constants.SPRING_PROFILE_QA,
            Constants.SPRING_PROFILE_PRODUCTION)) {
            initMetrics(servletContext, dispatcherTypes);

            // Forces browser to send cookies with HTTPS connection only
            servletContext.getSessionCookieConfig().setSecure(true);
        }

        servletContext.getSessionCookieConfig().setName(Constants.SESSION_COOKIE_NAME);
        servletContext.getSessionCookieConfig().setDomain(appConfiguration.get("cookieDomain"));

        log.info("Web application fully configured");
    }

    /**
     * Set up Mime types.
     */
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
        mappings.add("html", "text/html;charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", "text/html;charset=utf-8");

        mappings.add("svg", "image/svg+xml");
        mappings.add("ttf", "application/x-font-ttf");
        mappings.add("otf", "application/x-font-opentype");
        mappings.add("woff", "application/font-woff");
        mappings.add("woff2", "application/font-woff2");
        mappings.add("eot", "application/vnd.ms-fontobject");
        mappings.add("sfnt", "application/font-sfnt");

        container.setMimeMappings(mappings);
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
            metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
            metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
            new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        log.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet =
            servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/metrics/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }
}
