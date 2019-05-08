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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SisuStudyRegistryTest extends SpringTest {

    @Autowired
    @Qualifier("sisuStudyRegistry")
    private StudyRegistry sisuStudyRegistry;

    @Test
    public void shouldMapValuesCorrectly() {
        Person person = sisuStudyRegistry.getPerson("FooBar");
        assertThat(person.teacherNumber).isEqualTo("220272");
        assertThat(person.studentNumber).isEqualTo("012023965");
    }
}
