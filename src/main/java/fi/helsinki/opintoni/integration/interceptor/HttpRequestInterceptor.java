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

package fi.helsinki.opintoni.integration.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.exception.http.RestClientServiceException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;
    private final Environment env;

    public HttpRequestInterceptor(ObjectMapper objectMapper,
                                    Environment env) {
        this.objectMapper = objectMapper;
        this.env = env;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        /*
         * The following code cannot be run with test profile, because it uses MockClientHttpResponse
         * that does not allow getBody() to be called multiple times even when using BufferingClientHttpRequestFactory
         */
        if (env.acceptsProfiles(
            Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT,
            Constants.SPRING_PROFILE_DEMO,
            Constants.SPRING_PROFILE_QA,
            Constants.SPRING_PROFILE_PRODUCTION)) {

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                JsonHttpStatus jsonHttpStatus = objectMapper.readValue(response.getBody(), JsonHttpStatus.class);
                if (jsonHttpStatus.is5xxError()) {
                    throw new RestClientServiceException(
                        String.format("%s returned a %s response", request.getURI(), jsonHttpStatus.status));
                }
            }
        }
        return response;
    }
}
