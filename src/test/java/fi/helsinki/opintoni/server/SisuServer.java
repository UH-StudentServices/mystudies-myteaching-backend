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

package fi.helsinki.opintoni.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyAttainmentRequest;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.internal.DefaultObjectMapperFactory;
import org.mockserver.client.MockServerClient;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SisuServer {
    private final MockServerClient client;
    private final String graphQLApiPath;
    private final String sisuBaseUrl;

    public SisuServer(AppConfiguration appConfiguration, MockServerClient client) {
        this.client = client;
        this.sisuBaseUrl = appConfiguration.get("sisu.baseUrl");
        this.graphQLApiPath = appConfiguration.get("sisu.apiPath");
    }

    public void expectRolesRequest(String personId, String responseFile) throws Exception {
        Arguments arguments = new Arguments("private_person", new Argument("id", personId));

        client
            .when(
                request()
                    .withMethod("POST")
                    .withPath(graphQLApiPath)
                    .withBody(requestBodyMatcher(PrivatePersonRequest.class, arguments)))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(SampleDataFiles.toText(String.format("sisu/%s", responseFile))));
    }

    public void expectAttainmentRequest(String personId, String responseFile) throws Exception {
        Arguments arguments = new Arguments("private_person", new Argument("id", personId));

        client
            .when(
                request()
                    .withMethod("POST")
                    .withPath(graphQLApiPath)
                    .withBody(requestBodyMatcher(StudyAttainmentRequest.class, arguments)))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(SampleDataFiles.toText(String.format("sisu/%s", responseFile))));
    }

    private String requestBodyMatcher(Class requestClass, Arguments arguments) throws Exception {
        GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
            .url("http://localhost") // This is not actually used, but GraphQLRequestEntity build() method fails if left empty.
            .request(requestClass)
            .arguments(arguments)
            .build();

        SisuServerRequest sisuServerRequest = new SisuServerRequest(requestEntity.getRequest(), requestEntity.getVariables());

        DefaultObjectMapperFactory defaultObjectMapperFactory = new DefaultObjectMapperFactory();
        ObjectMapper mapper = defaultObjectMapperFactory.newSerializerMapper();

        return mapper.writeValueAsString(sisuServerRequest);
    }
}
