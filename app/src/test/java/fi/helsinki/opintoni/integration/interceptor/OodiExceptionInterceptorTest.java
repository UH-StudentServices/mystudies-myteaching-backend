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
import fi.helsinki.opintoni.exception.http.RestClientServiceException;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.InputStream;
import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OodiExceptionInterceptorTest {

    private final Environment env = mock(Environment.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);

    private OodiExceptionInterceptor oodiExceptionInterceptor = new OodiExceptionInterceptor(objectMapper, env);

    @Test(expected = RestClientServiceException.class)
    public void thatRestClientServiceExceptionIsThrown() throws Exception {
        HttpRequest httpRequest = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse clientHttpResponse = mock(ClientHttpResponse.class);
        JsonHttpStatus jsonHttpStatus = mock(JsonHttpStatus.class);

        when(objectMapper.readValue(any(InputStream.class), eq(JsonHttpStatus.class))).thenReturn(jsonHttpStatus);
        when(jsonHttpStatus.is5xxError()).thenReturn(true);
        when(execution.execute(eq(httpRequest), any())).thenReturn(clientHttpResponse);
        when(env.acceptsProfiles(Matchers.<String>anyVararg())).thenReturn(true);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(httpRequest.getURI()).thenReturn(new URI("https://oprek.helsinki.fi"));

        oodiExceptionInterceptor.intercept(httpRequest, new byte[0], execution);
    }
}
