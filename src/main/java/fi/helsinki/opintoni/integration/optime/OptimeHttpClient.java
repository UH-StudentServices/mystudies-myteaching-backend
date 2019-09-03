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

package fi.helsinki.opintoni.integration.optime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OptimeHttpClient implements OptimeClient {

    private static final Logger logger = LoggerFactory.getLogger(OptimeHttpClient.class);

    @Override
    public InputStream getICalendarContent(String feedUrl) {
        StopWatch stopWatch = new StopWatch();
        HttpClient httpClient = HttpClient.newBuilder()
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(feedUrl))
            .build();
        HttpResponse<InputStream> response = null;
        try {
            stopWatch.start();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            stopWatch.stop();
            logger.info("Response for {} took {} seconds", request.uri(), stopWatch.getTotalTimeSeconds());
        }

        return response.body();
    }
}
