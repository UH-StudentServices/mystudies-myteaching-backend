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

package fi.helsinki.opintoni.integration.sisu;

import com.google.common.collect.ImmutableMap;
import io.aexp.nodes.graphql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
public class SisuGraphQlClient implements SisuClient {

    private static final Logger log = LoggerFactory.getLogger(SisuGraphQlClient.class);

    private final RestTemplate restTemplate;

    @Value("${sisu.applicationAccountKey}")
    private String applicationAccountKey;
    @Value("${sisu.applicationAccountName}")
    private String applicationAccountName;
    @Value("${sisu.baseUrl}")
    private String baseUrl;
    @Value("${sisu.authPath}")
    private String authPath;
    @Value("${sisu.apiPath}")
    private String apiPath;

    @PostConstruct
    public void init() {
        String foo = "bar";
    }

    public SisuGraphQlClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    //@Cacheable(value = CacheConstants.SISU_PRIVATE_PERSON, cacheManager = "transientCacheManager", sync = true)
    public PrivatePerson getPrivatePerson(String id, String eduPersonPrincipalName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String token = getAuthenticationToken(eduPersonPrincipalName);

        stopWatch.stop();

        log.info("Response for authtoken took {} seconds", stopWatch.getTotalTimeSeconds());

        stopWatch.start();

        GraphQLTemplate graphQLTemplate = new GraphQLTemplate();
        try {
            GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
                .url(baseUrl + apiPath)
                .headers(ImmutableMap.of("Authorization", "Application " + token))
                .request(PrivatePerson.class)
                .arguments(new Arguments("private_person", new Argument("id", id)))
                .scalars(LocalDate.class)
                .build();

            GraphQLResponseEntity<PrivatePerson> responseEntity = graphQLTemplate.query(requestEntity, PrivatePerson.class);

            return responseEntity.getResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
            //Something went wrong
        } finally {
            stopWatch.stop();

            log.info("Response for query private_person took {} seconds", stopWatch.getTotalTimeSeconds());
        }
    }

    private String getAuthenticationToken(String edupersonPrincipalName) {
        Map<String, String> request = ImmutableMap.of(
            "username", applicationAccountName,
            "key", applicationAccountKey,
            "executingEppn", edupersonPrincipalName);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(new URI(baseUrl + authPath), request , AuthResponse.class);
            return response.getBody().authToken;
        } catch (Exception e) {
            throw new RuntimeException("Failed to autenticate");
        }
    }
}
