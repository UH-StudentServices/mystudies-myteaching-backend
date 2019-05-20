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
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OodiStudyRegistryConverter {

    public Enrollment oodiEnrollmentToEnrollment(OodiEnrollment oodiEnrollment) {
        Enrollment enrollment = new Enrollment();
        enrollment.credits = oodiEnrollment.credits;
        enrollment.typeCode = oodiEnrollment.typeCode;
        enrollment.rootId = oodiEnrollment.rootId;
        enrollment.webOodiUri = oodiEnrollment.webOodiUri;
        enrollment.endDate = oodiEnrollment.endDate;
        enrollment.isCancelled = oodiEnrollment.isCancelled;
        enrollment.learningOpportunityId = oodiEnrollment.learningOpportunityId;
        enrollment.name = oodiLocalizedValuesToLocalizedTexts(oodiEnrollment.name);
        enrollment.parentId = oodiEnrollment.parentId;
        enrollment.position = oodiEnrollment.position;
        enrollment.realisationId = oodiEnrollment.realisationId;
        enrollment.startDate = oodiEnrollment.startDate;

        return enrollment;
    }

    public Event oodiEventToEvent(OodiEvent oodiEvent) {
        Event event = new Event();
        event.optimeExtras = oodiEvent.optimeExtras;
        event.realisationId = oodiEvent.realisationId;
        event.buildingStreet = oodiEvent.buildingStreet;
        event.roomName = oodiEvent.roomName;
        event.startDate = oodiEvent.startDate;
        event.buildingZipCode = oodiEvent.buildingZipCode;
        event.endDate = oodiEvent.endDate;
        event.isCancelled = oodiEvent.isCancelled;
        event.realisationName = oodiLocalizedValuesToLocalizedTexts(oodiEvent.realisationName);
        event.realisationRootName = oodiLocalizedValuesToLocalizedTexts(oodiEvent.realisationRootName);
        event.typeCode = oodiEvent.typeCode;

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

    public TeacherCourse oodiTeacherCourseToTeacherCourse(OodiTeacherCourse oodiTeacherCourse) {
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.rootId = oodiTeacherCourse.rootId;
        teacherCourse.realisationName = oodiLocalizedValuesToLocalizedTexts(oodiTeacherCourse.realisationName);
        teacherCourse.realisationTypeCode = oodiTeacherCourse.realisationTypeCode;
        teacherCourse.realisationRootName = oodiLocalizedValuesToLocalizedTexts(oodiTeacherCourse.realisationRootName);
        teacherCourse.teacherRole = oodiTeacherCourse.teacherRole;
        teacherCourse.webOodiUri = oodiTeacherCourse.webOodiUri;
        teacherCourse.endDate = oodiTeacherCourse.endDate;
        teacherCourse.isCancelled = oodiTeacherCourse.isCancelled;
        teacherCourse.learningOpportunityId = oodiTeacherCourse.learningOpportunityId;
        teacherCourse.name = oodiLocalizedValuesToLocalizedTexts(oodiTeacherCourse.realisationName);
        teacherCourse.parentId = oodiTeacherCourse.parentId;
        teacherCourse.position = oodiTeacherCourse.position;
        teacherCourse.realisationId = oodiTeacherCourse.realisationId;
        teacherCourse.startDate = oodiTeacherCourse.startDate;
        teacherCourse.endDate = oodiTeacherCourse.endDate;

        return teacherCourse;
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
        }).collect(Collectors.toList());
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
        }).collect(Collectors.toList());
    }

    private List<LocalizedText> oodiLocalizedValuesToLocalizedTexts(List<OodiLocalizedValue> oodiLocalizedValues) {
        return oodiLocalizedValues.stream().map(oodiLocalizedValue -> {
            LocalizedText localizedText = new LocalizedText();
            localizedText.text = oodiLocalizedValue.text;
            localizedText.langcode = oodiLocaleToStudyRegistryLocale(oodiLocalizedValue.langcode);
            return localizedText;
        }).collect(Collectors.toList());
    }

    private StudyRegistryLocale oodiLocaleToStudyRegistryLocale(OodiLocale oodiLocale) {
        return StudyRegistryLocale.valueOf(oodiLocale.name());
    }
}
