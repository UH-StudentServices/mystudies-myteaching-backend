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
import fi.helsinki.opintoni.exception.http.BadRequestException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SpringPageMetaDataHttpClient implements PageMetaDataHttpClient{

    private final RestTemplate metaDataRestTemplate;

    public SpringPageMetaDataHttpClient(RestTemplate metaDataRestTemplate) {
        this.metaDataRestTemplate = metaDataRestTemplate;
    }

    @Override
    public String getPageBody(String pageUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Lists.newArrayList(MediaType.TEXT_HTML));
            headers.add("User-Agent", "Mozilla");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = metaDataRestTemplate.exchange(pageUrl, HttpMethod.GET, entity, String.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new Exception("Meta data request for url " + pageUrl + " failed with status code " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
