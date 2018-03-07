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

package fi.helsinki.opintoni.integration.pagemetadata;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class SpringPageMetaDataHttpClient implements PageMetaDataHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringPageMetaDataHttpClient.class);

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT = "Mozilla";
    private static final String PARAMETERS_KEY = "parameters";

    private final RestTemplate metaDataRestTemplate;

    public SpringPageMetaDataHttpClient(RestTemplate metaDataRestTemplate) {
        this.metaDataRestTemplate = metaDataRestTemplate;
    }

    @Override
    public Optional<String> getPageBody(String pageUrl) {
        Optional<String> pageBody = Optional.empty();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Lists.newArrayList(MediaType.TEXT_HTML));
            headers.add(USER_AGENT_KEY, USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>(PARAMETERS_KEY, headers);

            ResponseEntity<String> response = metaDataRestTemplate.exchange(pageUrl, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                pageBody = Optional.ofNullable(response.getBody());
            }
        } catch (Exception e) {
            LOGGER.error("Error when fetching pageBody from {}: {}", pageUrl, e.getMessage());
        }
        
        return pageBody;

    }
}
