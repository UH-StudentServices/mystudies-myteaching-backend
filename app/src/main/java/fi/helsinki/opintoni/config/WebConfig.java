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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.config.http.converter.CsvHttpMessageConverter;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.security.authorization.portfolio.PrivatePortfolioInterceptor;
import fi.helsinki.opintoni.security.authorization.portfolio.PublicPortfolioInterceptor;
import fi.helsinki.opintoni.security.authorization.portfolio.RestrictedPortfolioInterceptor;
import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.web.arguments.StudentNumberArgumentResolver;
import fi.helsinki.opintoni.web.arguments.TeacherNumberArgumentResolver;
import fi.helsinki.opintoni.web.arguments.UserIdArgumentResolver;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PrivatePortfolioInterceptor privatePortfolioInterceptor;

    @Autowired
    private RestrictedPortfolioInterceptor restrictedPortfolioInterceptor;

    @Autowired
    private PublicPortfolioInterceptor publicPortfolioInterceptor;

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.addAll(Lists.newArrayList(
            new AuthenticationPrincipalArgumentResolver(),
            new StudentNumberArgumentResolver(securityUtils),
            new TeacherNumberArgumentResolver(securityUtils),
            new UserIdArgumentResolver(userService, securityUtils)));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BufferedImageHttpMessageConverter());
        converters.add(new CsvHttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(privatePortfolioInterceptor)
            .addPathPatterns(RestConstants.PRIVATE_API_V1 + "/portfolio/**");

        registry
            .addInterceptor(restrictedPortfolioInterceptor)
            .addPathPatterns(RestConstants.RESTRICTED_API_V1 + "/portfolio/**");

        registry
            .addInterceptor(publicPortfolioInterceptor)
            .addPathPatterns(RestConstants.PUBLIC_API_V1 + "/portfolio/**");
    }
}
