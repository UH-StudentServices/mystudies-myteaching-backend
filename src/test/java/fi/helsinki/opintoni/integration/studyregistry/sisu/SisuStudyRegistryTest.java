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
