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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistry;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryResponse;

@Component
@Qualifier("sisuStudyRegistry")
public class SisuStudyRegistry implements StudyRegistry {

    private final SisuStudyRegistryConverter sisuStudyRegistryConverter;
    private final SisuClient sisuClient;

    @Autowired
    public SisuStudyRegistry(SisuClient sisuClient, SisuStudyRegistryConverter sisuStudyRegistryConverter) {
        this.sisuStudyRegistryConverter = sisuStudyRegistryConverter;
        this.sisuClient = sisuClient;
    }

    @Override
    public List<Enrollment> getEnrollments(String studentNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> getStudentEvents(String studentNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> getTeacherEvents(String personId) {
        return sisuStudyRegistryConverter.sisuCurSearchResultToEvents(
            sisuClient.curSearch(personId, LocalDate.now(ZoneId.of("Europe/Helsinki"))), personId);
    }

    @Override
    public List<StudyAttainment> getStudyAttainments(String personId) {
        Private_personQueryResponse res = sisuClient.getStudyAttainments(personId);
        return res.private_person().getAttainments().stream()
            .filter(a -> Objects.nonNull(a.getCourseUnit()))
            .map(sisuStudyRegistryConverter::sisuAttainmentToStudyAttainment)
            .collect(Collectors.toList());
    }

    @Override
    public List<StudyAttainment> getStudyAttainments(String personId, String studentNumber) {
        return getStudyAttainments(personId);
    }

    @Override
    public List<TeacherCourse> getTeacherCourses(String hloId, LocalDate since) {
        return sisuStudyRegistryConverter.sisuCURSearchResultToTeacherCourseList(sisuClient.curSearch(hloId, LocalDate.now()));
    }

    @Override
    public List<StudyRight> getStudentStudyRights(String studentNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Teacher> getCourseRealisationTeachers(String realisationId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Person getPerson(String personId) {
        return sisuStudyRegistryConverter.sisuPrivatePersonToPerson(sisuClient.getPrivatePerson(personId));
    }
}
