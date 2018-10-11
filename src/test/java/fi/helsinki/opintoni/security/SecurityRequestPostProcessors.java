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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class SecurityRequestPostProcessors {

    private SecurityRequestPostProcessors() {
    }

    public static SecurityContextRequestPostProcessor securityContext(SecurityContext securityContext) {
        return new SecurityContextRequestPostProcessor(securityContext);
    }

    private abstract static class SecurityContextRequestPostProcessorSupport {

        private final SecurityContextRepository repository = new HttpSessionSecurityContextRepository();

        final void save(SecurityContext securityContext, HttpServletRequest request) {
            HttpServletResponse response = new MockHttpServletResponse();

            HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
            this.repository.loadContext(requestResponseHolder);

            request = requestResponseHolder.getRequest();
            response = requestResponseHolder.getResponse();

            this.repository.saveContext(securityContext, request, response);
        }
    }

    public static final class SecurityContextRequestPostProcessor
        extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

        private final SecurityContext securityContext;

        private SecurityContextRequestPostProcessor(SecurityContext securityContext) {
            this.securityContext = securityContext;
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            save(this.securityContext, request);
            return request;
        }
    }
}
