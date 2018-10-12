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

import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight;
import fi.helsinki.opintoni.service.pilotdegreeprogramme.PilotDegreeProgrammeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PilotDegreeProgrammeResolver {

    private final OodiClient oodiClient;

    private final PilotDegreeProgrammeProperties pilotDegreeProgrammeProperties;

    @Autowired
    public PilotDegreeProgrammeResolver(OodiClient oodiClient,
                                        PilotDegreeProgrammeProperties pilotDegreeProgrammeProperties) {
        this.oodiClient = oodiClient;
        this.pilotDegreeProgrammeProperties = pilotDegreeProgrammeProperties;
    }

    public boolean isInPilotDegreeProgramme(String studentNumber) {
        List<OodiStudyRight> studyRights = oodiClient.getStudentStudyRights(studentNumber);
        return isInPilotDegreeProgramme(studyRights);
    }

    public boolean isInPilotDegreeProgramme(List<OodiStudyRight> studyRights) {
        List<String> pilotDegreeProgrammes = pilotDegreeProgrammeProperties.getPilotDegreeProgrammes();
        return studyRights.stream()
            .map(OodiStudyRight::getElementCodes)
            .flatMap(Collection::stream)
            .anyMatch(pilotDegreeProgrammes::contains);
    }

}
