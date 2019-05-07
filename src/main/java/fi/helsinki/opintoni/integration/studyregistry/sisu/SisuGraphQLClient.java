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

package fi.helsinki.opintoni.integration.studyregistry.sisu;

import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonRequest;
import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;

public class SisuGraphQLClient implements SisuClient {

    @Value("${sisu.baseUrl}")
    private String baseUrl;
    @Value("${sisu.apiPath}")
    private String apiPath;

    private static final Logger log = LoggerFactory.getLogger(SisuGraphQLClient.class);

    public PrivatePersonRequest getPrivatePerson(String id) {
        GraphQLTemplate graphQLTemplate = new GraphQLTemplate();

        try {
            GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
                .url(baseUrl + apiPath)
                .request(PrivatePersonRequest.class)
                .arguments(new Arguments("private_person", new Argument("id", id)))
                .build();
            GraphQLResponseEntity<PrivatePersonRequest> responseEntity = graphQLTemplate.query(requestEntity, PrivatePersonRequest.class);

            return responseEntity.getResponse();

        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }
}
