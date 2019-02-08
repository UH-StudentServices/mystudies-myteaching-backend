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

package fi.helsinki.opintoni.security;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Context provider which overrides request attributes with values of the reverse-proxy in front
 * of the local application. We need this because the front end runs on top of HTTPS while proxied
 * connections use HTTP.
 */
public class SAMLContextProviderReverseProxy extends SAMLContextProviderImpl {

    @Override
    protected void populateGenericContext(HttpServletRequest request,
                                          HttpServletResponse response,
                                          SAMLMessageContext context) throws MetadataProviderException {
        super.populateGenericContext(new ReverseProxyRequestWrapper(request), response, context);
    }

    public static class ReverseProxyRequestWrapper extends HttpServletRequestWrapper {

        private ReverseProxyRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public StringBuffer getRequestURL() {
            StringBuffer original = super.getRequestURL();
            if (original.indexOf("http:") == 0) {
                return original.replace(0, 4, "https");
            }
            return original;
        }

        @Override
        public boolean isSecure() {
            return true;
        }

    }

}