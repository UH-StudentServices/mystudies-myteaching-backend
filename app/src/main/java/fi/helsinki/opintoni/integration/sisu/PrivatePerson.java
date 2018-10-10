package fi.helsinki.opintoni.integration.sisu;

import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;

import java.util.List;

@GraphQLProperty(
    name="private_person",
    arguments={@GraphQLArgument(name="id", type="String")})
public class PrivatePerson {
    public List<Enrolment> enrolments;
    public List<Attainment> attainments;
}
