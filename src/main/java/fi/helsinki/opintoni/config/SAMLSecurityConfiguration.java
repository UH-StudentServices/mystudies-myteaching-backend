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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderLB;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class SAMLSecurityConfiguration {

    @Value("${hostUrl}")
    private String hostUrl;

    @Bean("contextProvider")
    @Profile({
        Constants.SPRING_PROFILE_QA,
        Constants.SPRING_PROFILE_PRODUCTION
    })
    public SAMLContextProviderLB contextProvider() throws URISyntaxException {
        SAMLContextProviderLB contextProvider =  new SAMLContextProviderLB();
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
        SAMLContextProviderLB contextProvider =  new SAMLContextProviderLB();
        URI uri = new URI(hostUrl);
        contextProvider.setScheme(uri.getScheme());
        contextProvider.setServerPort(uri.getPort());
        contextProvider.setServerName(uri.getHost());
        contextProvider.setContextPath("");
        contextProvider.setIncludeServerPortInRequestURL(true);
        return contextProvider;
    }
}
