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
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

public class OodiSisuClient implements OodiClient {
    private static final String EXAM_ASSESSMENT_ITEM_TYPE = "urn:code:assessment-item-type:exam";
    private static final String EXAM_SSG_NAME = "Tentti";
    private static final String COURSE_REALISATION_ID_PREFIX = "hy-CUR-";
    private static final Integer TYPE_LECTURE_COURSE = 5;

    private final SisuRestClient sisuRestClient;
    private final String sisuPersonId;
    private final Integer examTypeCode;

    public OodiSisuClient(SisuRestClient sisuRestClient, Environment environment) {
        this.sisuRestClient = sisuRestClient;
        this.sisuPersonId = environment.getProperty("testSisuPersonId");
        this.examTypeCode = Integer.valueOf( (String) (environment.getProperty("courses.examTypeCodes", List.class).get(0)));
    }

    @Override
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        List<Enrolment> enrollments = sisuRestClient.getEnrolments(sisuPersonId);

        return enrollments.stream()
            .map(this::enrolmentToOodiEnrollment)
            .collect(Collectors.toList());
    }

    @Override
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        List<Enrolment> enrolments = sisuRestClient.getEnrolments(sisuPersonId);

        return enrolments.stream()
            .flatMap(this::expandOodiEventsFromEnrollmentStudyEvents)
            .collect(Collectors.toList());
    }

    @Override
    public List<OodiEvent> getTeacherEvents(String teacherNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        return new ArrayList<>();
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

    private OodiEnrollment enrolmentToOodiEnrollment(Enrolment enrolment) {
        OodiEnrollment oodiEnrollment = new OodiEnrollment();

        CourseUnitRealisation courseUnitRealisation = sisuRestClient.getCourseUnitRealisation(enrolment.courseUnitRealisationId);
        Assessment assessment = sisuRestClient.getAssessment(enrolment.assessmentItemId);

        oodiEnrollment.name = localizedStringToOodiLocalizedValueList(courseUnitRealisation.name);
        oodiEnrollment.learningOpportunityId = enrolment.courseUnitId;

        if(isExam(assessment)) {
            oodiEnrollment.typeCode = examTypeCode;
            //start and end date for exam???
        } else {
            oodiEnrollment.typeCode = TYPE_LECTURE_COURSE;
            oodiEnrollment.startDate = courseUnitRealisation.activityPeriod.startDate.atStartOfDay();
            oodiEnrollment.endDate = courseUnitRealisation.activityPeriod.startDate.atStartOfDay();
        }

        oodiEnrollment.realisationId = enrolment.courseUnitRealisationId.replace(COURSE_REALISATION_ID_PREFIX, "");
        oodiEnrollment.position = Position.ROOT.getValue();
        oodiEnrollment.credits = assessment.credits.max;

        return oodiEnrollment;

    }

    private Stream<OodiEvent> expandOodiEventsFromEnrollmentStudyEvents(Enrolment enrolment) {
        CourseUnitRealisation courseUnitRealisation = sisuRestClient.getCourseUnitRealisation(enrolment.courseUnitRealisationId);

        if(enrolment.studySubGroupIds != null) {
            return enrolment.studySubGroupIds.stream()
                .map(ssgId -> getStudySubGroupById(ssgId, courseUnitRealisation))
                .flatMap(ssg ->
                    findStudyEventsForSSG(ssg)
                        .flatMap(studyEvent -> expandRecurringStudyEventToOodiEvents(studyEvent, ssg, courseUnitRealisation)));
        } else {
            return Stream.empty();
        }
    }

    private Stream<StudyEvent> findStudyEventsForSSG(StudySubGroup studySubGroup) {
        return studySubGroup.studyEventIds.stream().map(sisuRestClient::getStudyEvent);
    }

    private StudySubGroup getStudySubGroupById(String ssgId, CourseUnitRealisation courseUnitRealisation) {
        return courseUnitRealisation.studyGroupSets.stream()
            .flatMap(sgs -> sgs.studySubGroups.stream())
            .filter(ssg -> ssgId.equals(ssg.localId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("SSG not found for id"));
    }

    private Stream<OodiEvent> expandRecurringStudyEventToOodiEvents(
        StudyEvent studyEvent,
        StudySubGroup studySubGroup,
        CourseUnitRealisation courseUnitRealisation) {

        Location location = !studyEvent.locationIds.isEmpty() ? sisuRestClient.getLocation(studyEvent.locationIds.get(0)) : null;
        Building building = location != null ? sisuRestClient.getBuilding(location.buildingId) : null;

        if(studyEvent.recursEvery.equals(Interval.NEVER)) {
            return Stream.of(studyEventToOodiEvent(studyEvent, studySubGroup, courseUnitRealisation, location, building));
        } else if(studyEvent.recursUntil != null) {
            return expandStudyEvent(studyEvent).stream()
                .map(s -> studyEventToOodiEvent(s, studySubGroup, courseUnitRealisation, location, building));
        } else {
            throw new RuntimeException("Recurring event has no end date!");
        }
    }

    private List<StudyEvent> expandStudyEvent(StudyEvent studyEvent) {

        List<StudyEvent> expandedStudyEvents = new ArrayList<>();

        StudyEvent currentStudyEvent = studyEvent;
        LocalDateTime maxDateTime = studyEvent.recursUntil.atTime(23, 59);

        while(currentStudyEvent.startTime.isBefore(maxDateTime) || currentStudyEvent.startTime.isEqual(maxDateTime)) {
            expandedStudyEvents.add(currentStudyEvent);

            currentStudyEvent = new StudyEvent(currentStudyEvent);
        }

        return expandedStudyEvents;

    }

    private OodiEvent studyEventToOodiEvent(
        StudyEvent studyEvent,
        StudySubGroup studySubGroup,
        CourseUnitRealisation courseUnitRealisation,
        Location location,
        Building building) {
        OodiEvent oodiEvent = new OodiEvent();
        oodiEvent.realisationName = localizedStringToOodiLocalizedValueList(studySubGroup.name);
        oodiEvent.realisationRootName = localizedStringToOodiLocalizedValueList(courseUnitRealisation.name);
        oodiEvent.realisationId = Integer.valueOf(courseUnitRealisation.id.replace(COURSE_REALISATION_ID_PREFIX, ""));

        if(EXAM_SSG_NAME.equals(studySubGroup.name.fi)) {
            oodiEvent.typeCode = examTypeCode;
        }

        Duration duration = Duration.parse(studyEvent.duration);

        oodiEvent.startDate = studyEvent.startTime;
        oodiEvent.endDate = studyEvent.startTime.plusSeconds(duration.getSeconds());
        oodiEvent.buildingStreet = building.address.streetAddress;
        oodiEvent.buildingZipCode = building.address.postalCode;
        oodiEvent.roomName = location.name.fi;

        return oodiEvent;
    }

    private List<OodiLocalizedValue> localizedStringToOodiLocalizedValueList(LocalizedString localizedString) {

        return newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, localizedString.fi),
            new OodiLocalizedValue(OodiLocale.SV, localizedString.sv),
            new OodiLocalizedValue(OodiLocale.EN, localizedString.en));
    }

    private boolean isExam(Assessment assessment) {
       return EXAM_ASSESSMENT_ITEM_TYPE.equals(assessment.assessmentItemType);
    }
}
