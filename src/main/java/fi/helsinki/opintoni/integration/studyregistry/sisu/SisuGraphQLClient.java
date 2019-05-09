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
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyAttainmentRequest;
import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;

import java.net.MalformedURLException;

import static fi.helsinki.opintoni.integration.studyregistry.sisu.Constants.SISU_PRIVATE_PERSON_ID_PREFIX;

public class SisuGraphQLClient implements SisuClient {
    private final String endPointURL;

    public SisuGraphQLClient(String endPointURL) {
        this.endPointURL = endPointURL;
    }

    @Override
    public PrivatePersonRequest getPrivatePerson(String id) {

        return queryWithPrivatePersonId(id, PrivatePersonRequest.class);

    }

    @Override
    public StudyAttainmentRequest getStudyAttainments(String id) {
        return queryWithPrivatePersonId(id, StudyAttainmentRequest.class);
    }

    private <T> T queryWithPrivatePersonId(String id, Class<T> type) {

        if (!id.startsWith(SISU_PRIVATE_PERSON_ID_PREFIX)) {
            id = SISU_PRIVATE_PERSON_ID_PREFIX + id;
        }

        GraphQLTemplate graphQLTemplate = new GraphQLTemplate();
        try {
            GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
                .url(endPointURL)
                .request(type)
                .arguments(new Arguments("private_person", new Argument("id", id)))
                .build();

            GraphQLResponseEntity<T> responseEntity = graphQLTemplate.query(requestEntity, type);

            return responseEntity.getResponse();
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }

}
