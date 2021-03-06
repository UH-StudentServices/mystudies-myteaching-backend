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
import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import fi.helsinki.opintoni.integration.studyregistry.Organisation;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class OodiStudyRegistryConverter {

    public Enrollment oodiEnrollmentToEnrollment(OodiEnrollment oodiEnrollment) {
        Enrollment enrollment = new Enrollment();
        enrollment.credits = oodiEnrollment.credits;
        enrollment.typeCode = oodiEnrollment.typeCode;
        enrollment.rootId = oodiEnrollment.rootId;
        enrollment.isCancelled = oodiEnrollment.isCancelled;
        enrollment.learningOpportunityId = oodiEnrollment.learningOpportunityId;
        enrollment.name = oodiLocalizedValuesToLocalizedTexts(oodiEnrollment.name);
        enrollment.parentId = oodiEnrollment.parentId;
        enrollment.rootId = oodiEnrollment.rootId;
        enrollment.position = oodiEnrollment.position;
        enrollment.isHidden = oodiEnrollment.isHidden;
        enrollment.realisationId = oodiEnrollment.realisationId;
        enrollment.startDate = oodiEnrollment.startDate;
        enrollment.endDate = oodiEnrollment.endDate;
        enrollment.organisations = oodiEnrollment.organisations.stream()
            .map(org -> new Organisation(org.code, oodiLocalizedValuesToLocalizedTexts(org.name)))
            .collect(toList());

        return enrollment;
    }


    // XXX used for student events only, are these even needed anymore?
    public Event oodiEventToEvent(OodiEvent oodiEvent) {
        Event event = new Event();
        event.optimeExtras = oodiEvent.optimeExtras;
        event.realisationId = String.valueOf(oodiEvent.realisationId);
        event.buildingStreet = oodiEvent.buildingStreet;
        event.roomName = oodiEvent.roomName;
        event.startDate = oodiEvent.startDate;
        event.buildingZipCode = oodiEvent.buildingZipCode;
        event.endDate = oodiEvent.endDate;
        event.isCancelled = oodiEvent.isCancelled;
        event.realisationName = oodiLocalizedValuesToLocalizedTexts(oodiEvent.realisationName);
        event.realisationRootName = oodiLocalizedValuesToLocalizedTexts(oodiEvent.realisationRootName);
        event.typeCode = oodiEvent.typeCode;
        event.isHidden = oodiEvent.isHidden;
        return event;
    }

    public StudyAttainment oodiStudyAttainmentToStudyAttainment(OodiStudyAttainment oodiStudyAttainment) {
        StudyAttainment studyAttainment = new StudyAttainment();
        studyAttainment.studyAttainmentId = oodiStudyAttainment.studyAttainmentId.toString();
        studyAttainment.attainmentDate = oodiStudyAttainment.attainmentDate;
        studyAttainment.credits = oodiStudyAttainment.credits;
        studyAttainment.grade = oodiLocalizedValuesToLocalizedTexts(oodiStudyAttainment.grade);
        studyAttainment.learningOpportunityName = oodiLocalizedValuesToLocalizedTexts(oodiStudyAttainment.learningOpportunityName);
        studyAttainment.teachers = oodiTeachersToTeachers(oodiStudyAttainment.teachers);

        return studyAttainment;
    }

    public StudyRight oodiStudyRightToStudyRight(OodiStudyRight oodiStudyRight) {
        StudyRight studyRight = new StudyRight();
        studyRight.elements = oodiStudyRight.elements;
        studyRight.faculty = oodiStudyRight.faculty;
        studyRight.priority = oodiStudyRight.priority;

        return studyRight;
    }

    public List<Teacher> oodiCourseUnitRealisationTeachersToTeachers(List<OodiCourseUnitRealisationTeacher> oodiCourseUnitRealisationTeachers) {
        return oodiCourseUnitRealisationTeachers.stream().map(oodiCourseUnitRealisationTeacher -> {
            Teacher teacher = new Teacher();
            teacher.name = oodiCourseUnitRealisationTeacher.fullName;

            return teacher;
        }).collect(toList());
    }

    public Person oodiRolesToPerson(OodiRoles oodiRoles) {
        Person person = new Person();
        person.studentNumber = oodiRoles.studentNumber;
        person.teacherNumber = oodiRoles.teacherNumber;

        return person;
    }

    private List<Teacher> oodiTeachersToTeachers(List<OodiTeacher> oodiTeachers) {
        return oodiTeachers.stream().map(oodiTeacher -> {
            Teacher teacher = new Teacher();
            teacher.name = oodiTeacher.shortName;

            return teacher;
        }).collect(toList());
    }

    private List<LocalizedText> oodiLocalizedValuesToLocalizedTexts(List<OodiLocalizedValue> oodiLocalizedValues) {
        return oodiLocalizedValues.stream().map(oodiLocalizedValue -> {
            LocalizedText localizedText = new LocalizedText();
            localizedText.text = oodiLocalizedValue.text;
            localizedText.langcode = oodiLocaleToStudyRegistryLocale(oodiLocalizedValue.langcode);
            return localizedText;
        }).collect(toList());
    }

    private StudyRegistryLocale oodiLocaleToStudyRegistryLocale(OodiLocale oodiLocale) {
        return StudyRegistryLocale.valueOf(oodiLocale.name());
    }
}
