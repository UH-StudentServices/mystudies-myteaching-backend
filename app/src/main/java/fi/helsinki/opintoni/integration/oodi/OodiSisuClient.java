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

package fi.helsinki.opintoni.integration.oodi;

import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.Position;
import fi.helsinki.opintoni.integration.sisu.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.format.DateTimeFormatter.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class OodiSisuClient implements OodiClient {
    private static final List<String> EXAM_COURSE_UNIT_REALISATION_TYPES = newArrayList(
        "urn:code:course-unit-realisation-type:exam-final",
        "urn:code:course-unit-realisation-type:exam-exam");
    private static final String COURSE_REALISATION_ID_PREFIX = "hy-CUR-";
    private static final Integer TYPE_LECTURE_COURSE = 5;
    private static final String COURSE_UNIT_ATTAINMENT_TYPE = "CourseUnitAttainment";

    private final SisuClient sisuClient;

    @Value("#{'${courses.examTypeCodes}'.split(',')}")
    private List<Integer> examTypeCodes;

    @Value("#{${sisu.userDetailsMap}}")
    private Map<String, List<String>> userDetailsMap;

    public OodiSisuClient(SisuClient sisuClient) {
        this.sisuClient = sisuClient;
    }

    @Override
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        PrivatePerson privatePerson = getPrivatePersonDataForStudentNumber(studentNumber);

        List<OodiEnrollment> oodiEnrollments = privatePerson.enrolments.stream()
            .filter(enrolment -> !isExam(enrolment.courseUnitRealisation))
            .map(this::enrolmentToOodiEnrollment)
            .collect(Collectors.toList());

        return oodiEnrollments;
    }

    @Override
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        PrivatePerson privatePerson = getPrivatePersonDataForStudentNumber(studentNumber);

        return privatePerson.enrolments.stream()
            .flatMap(this::getOodiEventsForEnrolment)
            .collect(Collectors.toList());
    }

    @Override
    public List<OodiEvent> getTeacherEvents(String teacherNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        PrivatePerson privatePerson = getPrivatePersonDataForStudentNumber(studentNumber);

        return privatePerson.attainments.stream()
            .filter(attainment -> COURSE_UNIT_ATTAINMENT_TYPE.equals(attainment.type))
            .map(attainment -> {
            CourseUnit courseUnit = attainment.courseUnit;

            OodiStudyAttainment oodiStudyAttainment = new OodiStudyAttainment();

            oodiStudyAttainment.attainmentDate = LocalDate.parse(attainment.attainmentDate, ISO_LOCAL_DATE).atStartOfDay();
            oodiStudyAttainment.credits = attainment.credits.intValue();
            oodiStudyAttainment.learningOpportunityName = localizedStringToOodiLocalizedValueList(courseUnit.name);

            Grade grade = attainment.gradeScale.grades.stream().filter(g -> attainment.gradeId.equals(g.localId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid grade"));

            oodiStudyAttainment.grade = localizedStringToOodiLocalizedValueList(grade.abbreviation);

            if (attainment.verifierPerson != null) { // This can be empty. Why?
                OodiTeacher oodiTeacher = new OodiTeacher();
                oodiTeacher.shortName = String.join(" ", attainment.verifierPerson.firstName, attainment.verifierPerson.lastName);
                oodiStudyAttainment.teachers = newArrayList(oodiTeacher);
            }

            return oodiStudyAttainment;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiCourseUnitRealisationTeacher> getCourseUnitRealisationTeachers(String realisationId) {
        //Now part of the getEnrollments
        return new ArrayList<>();
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return null;
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return new OodiRoles();
    }

    @Override
    public OodiLearningOpportunity getLearningOpportunity(String learningOpportunityId) {
        return new OodiLearningOpportunity(); //May not be needed anymore because Course recommendations have been removed?
    }

    private PrivatePerson getPrivatePersonDataForStudentNumber(String studentNumber) {
        List<String> userDetails = userDetailsMap.get(studentNumber);
        if(userDetails == null) {
            throw new RuntimeException("Sisu user details not found for studentNumber " + studentNumber);
        }

        return sisuClient.getPrivatePerson(userDetails.get(0), userDetails.get(1));
    }

    private OodiEnrollment enrolmentToOodiEnrollment(Enrolment enrolment) {
        OodiEnrollment oodiEnrollment = new OodiEnrollment();
        oodiEnrollment.name = localizedStringToOodiLocalizedValueList(enrolment.courseUnitRealisation.name);
        oodiEnrollment.learningOpportunityId = enrolment.courseUnit.id;

        oodiEnrollment.typeCode = TYPE_LECTURE_COURSE;
        oodiEnrollment.startDate = LocalDate.parse(enrolment.courseUnitRealisation.activityPeriod.startDate, ISO_LOCAL_DATE).atStartOfDay();
        oodiEnrollment.endDate = LocalDate.parse(enrolment.courseUnitRealisation.activityPeriod.endDate, ISO_LOCAL_DATE).atStartOfDay();

        oodiEnrollment.realisationId =  enrolment.courseUnitRealisation.id.replace(COURSE_REALISATION_ID_PREFIX, "");
        oodiEnrollment.position = Position.ROOT.getValue();
        oodiEnrollment.credits = enrolment.courseUnit.credits.max;

        return oodiEnrollment;

    }

    private Stream<OodiEvent> getOodiEventsForEnrolment(Enrolment enrolment) {
        return enrolment.studySubGroups.stream()
           .flatMap(enrolmentStudySubGroup ->
               enrolment.courseUnitRealisation.studyGroupSets.stream().flatMap(studyGroupSet ->
                   studyGroupSet.studySubGroups.stream().filter(ssg -> enrolmentStudySubGroup.studySubGroupId.equals(ssg.id))))
           .flatMap(studySubGroup -> studySubGroup.studyEvents.stream())
           .flatMap(studyEvent -> studyEvent.events.stream().map(studyEventRealisation ->
               eventToOodiEvent(studyEvent, studyEventRealisation, enrolment.courseUnitRealisation)));
    }

    private OodiEvent eventToOodiEvent(
        StudyEvent studyEvent,
        StudyEventRealisation studyEventRealisation,
        CourseUnitRealisation courseUnitRealisation) {

        OodiEvent oodiEvent = new OodiEvent();
        oodiEvent.typeCode = isExam(courseUnitRealisation) ? examTypeCodes.get(0) : null;
        oodiEvent.realisationRootName = localizedStringToOodiLocalizedValueList(courseUnitRealisation.name);
        oodiEvent.realisationName = studyEvent.name != null ? localizedStringToOodiLocalizedValueList(studyEvent.name) : oodiEvent.realisationRootName;  // This can be empty. Why?
        oodiEvent.realisationId = Integer.valueOf(courseUnitRealisation.id.replace(COURSE_REALISATION_ID_PREFIX, ""));

        oodiEvent.startDate = LocalDateTime.parse(studyEventRealisation.start, ISO_LOCAL_DATE_TIME);
        oodiEvent.endDate = LocalDateTime.parse(studyEventRealisation.end, ISO_LOCAL_DATE_TIME);
        oodiEvent.buildingStreet = !studyEvent.locations.isEmpty() ? studyEvent.locations.get(0).building.address.streetAddress : null;
        oodiEvent.buildingZipCode = !studyEvent.locations.isEmpty() ? studyEvent.locations.get(0).building.address.postalCode : null;
        oodiEvent.roomName = !studyEvent.locations.isEmpty() ? studyEvent.locations.get(0).name.fi: null;

        return oodiEvent;
    }

    private List<OodiLocalizedValue> localizedStringToOodiLocalizedValueList(LocalizedString localizedString) {

        return newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, localizedString.fi),
            new OodiLocalizedValue(OodiLocale.SV, localizedString.sv),
            new OodiLocalizedValue(OodiLocale.EN, localizedString.en));
    }

    private boolean isExam(CourseUnitRealisation courseUnitRealisation) {
       return EXAM_COURSE_UNIT_REALISATION_TYPES.contains(courseUnitRealisation.courseUnitRealisationTypeUrn);
    }
}
