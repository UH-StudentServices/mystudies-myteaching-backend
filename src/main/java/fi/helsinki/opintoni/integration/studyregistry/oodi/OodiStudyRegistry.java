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

package fi.helsinki.opintoni.integration.studyregistry.oodi;

import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistry;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("oodiStudyRegistry")
public class OodiStudyRegistry implements StudyRegistry {

    private final OodiClient oodiClient;
    private final OodiStudyRegistryConverter oodiStudyRegistryConverter;

    @Autowired
    public OodiStudyRegistry(OodiClient oodiClient,
                             OodiStudyRegistryConverter oodiStudyRegistryConverter) {
        this.oodiClient = oodiClient;
        this.oodiStudyRegistryConverter = oodiStudyRegistryConverter;
    }

    @Override
    public List<Enrollment> getEnrollments(String studentNumber) {
        List<OodiEnrollment> oodiEnrollments = oodiClient.getEnrollments(studentNumber);

        return oodiEnrollments.stream()
            .map(oodiStudyRegistryConverter::oodiEnrollmentToEnrollment)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> getStudentEvents(String studentNumber) {
        List<OodiEvent> oodiEvents = oodiClient.getStudentEvents(studentNumber);

        return oodiEvents.stream()
            .map(oodiStudyRegistryConverter::oodiEventToEvent)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> getTeacherEvents(String teacherNumber) {
        throw new UnsupportedOperationException("use sisu");
    }

    @Override
    public List<StudyAttainment> getStudyAttainments(String personId) {
        String studentNumber = getPerson(personId).studentNumber;

        return getStudyAttainmentsFromOodi(studentNumber);
    }

    @Override
    public List<StudyAttainment> getStudyAttainments(String personId, String studentNumber) {
        return getStudyAttainmentsFromOodi(studentNumber);
    }

    private List<StudyAttainment> getStudyAttainmentsFromOodi(String studentNumber) {
        List<OodiStudyAttainment> oodiStudyAttainments = oodiClient.getStudyAttainments(studentNumber);

        return oodiStudyAttainments.stream()
            .map(oodiStudyRegistryConverter::oodiStudyAttainmentToStudyAttainment)
            .collect(Collectors.toList());
    }

    @Override
    public List<TeacherCourse> getTeacherCourses(String teacherNumber, LocalDate since) {
        throw new UnsupportedOperationException("use sisu");
    }

    @Override
    public List<StudyRight> getStudentStudyRights(String studentNumber) {
        List<OodiStudyRight> oodiStudyRights = oodiClient.getStudentStudyRights(studentNumber);

        return oodiStudyRights.stream()
            .map(oodiStudyRegistryConverter::oodiStudyRightToStudyRight)
            .collect(Collectors.toList());
    }

    @Override
    public List<Teacher> getCourseRealisationTeachers(String realisationId) {
        return oodiStudyRegistryConverter
            .oodiCourseUnitRealisationTeachersToTeachers(oodiClient.getCourseUnitRealisationTeachers(realisationId));
    }

    // Only works with persons migrated from Oodi, as their IDs start with the known prefix.
    // Profile must and will be switched to integrate to Sisu instead of Oodi before
    // Sisu native persons appear.
    private String toOodiId(String personId) {
        String sisuPrefix = "hy-hlo-";
        if (personId.startsWith(sisuPrefix)) {
            personId = personId.replace(sisuPrefix, "");
        }
        // Check that we have an integer:
        Long.valueOf(personId);
        return personId;
    }

    @Override
    public Person getPerson(String personId) {
        OodiRoles oodiRoles = oodiClient.getRoles(toOodiId(personId));

        return oodiStudyRegistryConverter.oodiRolesToPerson(oodiRoles);
    }
}
