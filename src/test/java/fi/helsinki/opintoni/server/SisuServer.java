package fi.helsinki.opintoni.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonRequest;
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

    private String requestBodyMatcher(Class requestClass, Arguments arguments) throws Exception {
        GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
            .url(sisuBaseUrl)
            .request(requestClass)
            .arguments(arguments)
            .build();

        SisuServerRequest sisuServerRequest = new SisuServerRequest(requestEntity.getRequest(), requestEntity.getVariables());

        DefaultObjectMapperFactory defaultObjectMapperFactory = new DefaultObjectMapperFactory();
        ObjectMapper mapper = defaultObjectMapperFactory.newSerializerMapper();

        return mapper.writeValueAsString(sisuServerRequest);
    }
}
