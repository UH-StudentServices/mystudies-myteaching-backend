package fi.helsinki.opintoni.integration.studyregistry.sisu;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class SisuStudyRegistryTest extends SpringTest {

    @Autowired
    private SisuStudyRegistry sisuStudyRegistry;

    @Test
    public void thatPrivatePersonIsReturned() throws Exception {
        final String personId = "hy-1234567";

        sisuServer.expectRolesRequest(
            personId,
            "private_person_response.json");

        Person person = sisuStudyRegistry.getPerson(personId);

        assertThat(person.studentNumber).isEqualTo("123456");
        assertThat(person.teacherNumber).isEqualTo("654321");
    }


}
