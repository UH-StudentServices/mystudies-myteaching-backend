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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import org.junit.Before;
import org.junit.Test;

import static fi.helsinki.opintoni.integration.studyregistry.sisu.Constants.SISU_PRIVATE_PERSON_ID_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

public class SisuStudyRegistryTest extends SpringTest {

    private static final String PERSON_ID = "1234567";
    private static final String PREFIXED_PERSON_ID = SISU_PRIVATE_PERSON_ID_PREFIX + PERSON_ID;

    private SisuStudyRegistry sisuStudyRegistry;

    @Before
    public void initSisuStudyRegistry() {
        SisuGraphQLClient sisuGraphQLClient =
            new SisuGraphQLClient(String.format("http://localhost:%s/graphql", mockServerRule.getPort()));

        sisuStudyRegistry = new SisuStudyRegistry(sisuGraphQLClient, new SisuStudyRegistryConverter());
    }

    private void assertGetPerson(String personId, String requestPersonId) throws Exception {
        sisuServer.expectRolesRequest(
            requestPersonId,
            "private_person_response.json");

        Person person = sisuStudyRegistry.getPerson(personId);

        assertThat(person.studentNumber).isEqualTo("123456");
        assertThat(person.teacherNumber).isEqualTo("654321");
    }

    @Test
    public void thatPersonDataIsReturnedForPrefixedPersonId() throws Exception {
        assertGetPerson(PREFIXED_PERSON_ID, PREFIXED_PERSON_ID);
    }

    @Test
    public void thatPrefixIsAddedAndPersonDataIsReturnedForNonPrefixedPersonId() throws Exception {
        assertGetPerson(PERSON_ID, PREFIXED_PERSON_ID);
    }
}
