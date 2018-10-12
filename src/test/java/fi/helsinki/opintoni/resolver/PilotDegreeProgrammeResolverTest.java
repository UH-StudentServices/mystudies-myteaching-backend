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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class PilotDegreeProgrammeResolverTest extends SpringTest {

    @Autowired
    private PilotDegreeProgrammeResolver resolver;

    @Test
    public void thatTrueIsReturnedForPilotProgramParticipant() {
        defaultStudentRequestChain().studyRights("studentstudyrights.json");

        assertThat(resolver.isInPilotDegreeProgramme(TestConstants.STUDENT_NUMBER)).isTrue();
    }

    @Test
    public void thatFalseIsReturnedForNormalStudent() {
        defaultStudentRequestChain().studyRights("studentstudyrightswithnopilotdegreeprogramme.json");

        assertThat(resolver.isInPilotDegreeProgramme(TestConstants.STUDENT_NUMBER)).isFalse();
    }

}
