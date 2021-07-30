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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import fi.helsinki.opintoni.integration.studyregistry.OptimeExtras;
import fi.helsinki.opintoni.integration.studyregistry.Organisation;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AcceptorPersonTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AttainmentTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeScaleTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedStringTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PublicPersonTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudySubGroupTO;
import fi.helsinki.opintoni.util.FunctionHelper;

@Component
public class SisuStudyRegistryConverter {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    static DateTimeFormatter sisuDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final Logger logger = LoggerFactory.getLogger(SisuStudyRegistryConverter.class);

    private static final Map<String, Integer> SISU_CUR_TYPE_TO_TYPE_CODE;
    private static final Integer DEFAULT_CUR_TYPE_CODE = 17;

    static {
        Map<String, Integer> typeMapping = new HashMap<>();
        typeMapping.put("urn:code:course-unit-realisation-type:independent-work-essay", 21);
        typeMapping.put("urn:code:course-unit-realisation-type:independent-work-presentation", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-seminar", 10);
        typeMapping.put("urn:code:course-unit-realisation-type:licentiate-thesis", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-lab", 22);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-online", 26);
        typeMapping.put("urn:code:course-unit-realisation-type:training-training", 3);
        typeMapping.put("urn:code:course-unit-realisation-type:exam-exam", 19);
        typeMapping.put("urn:code:course-unit-realisation-type:exam-electronic", 28);
        typeMapping.put("urn:code:course-unit-realisation-type:thesis-doctoral", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:independent-work-project", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-field-course", 12);
        typeMapping.put("urn:code:course-unit-realisation-type:thesis-bachelor", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-individual-teaching", 17);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-project", 17);
        typeMapping.put("urn:code:course-unit-realisation-type:thesis-masters", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:exam-midterm", 19);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-lectures", 17);
        typeMapping.put("urn:code:course-unit-realisation-type:exam-final", 19);
        typeMapping.put("urn:code:course-unit-realisation-type:independent-work-learning-diary", 99);
        typeMapping.put("urn:code:course-unit-realisation-type:teaching-participation-small-group", 17);

        SISU_CUR_TYPE_TO_TYPE_CODE = typeMapping;
    }

    public StudyAttainment sisuAttainmentToStudyAttainment(AttainmentTO attainment) {
        StudyAttainment studyAttainment = new StudyAttainment();
        studyAttainment.attainmentDate = LocalDate
            .parse(attainment.getAttainmentDate(), DateTimeFormatter.ofPattern(DATE_PATTERN)).atStartOfDay();
        studyAttainment.credits = attainment.getCredits().intValue();
        studyAttainment.grade = sisuGradeScaleToLocalizedTextList(attainment.getGradeScale(), attainment.getGradeId());
        studyAttainment.studyAttainmentId = attainment.getId();
        if (attainment.getCourseUnit() != null) {
            studyAttainment.learningOpportunityName = localizedStringToToLocalizedText(attainment.getCourseUnit().getName());
        }
        studyAttainment.teachers = Optional.ofNullable(attainment.getAcceptorPersons()).stream()
            .flatMap(List::stream)
            .map(this::sisuAcceptorPersonToTeacher)
            .collect(Collectors.toList());
        return studyAttainment;
    }

    public List<LocalizedText> sisuGradeScaleToLocalizedTextList(GradeScaleTO scale, String gradeId) {
        GradeTO grade = scale.getGrades().stream()
            .filter((g) -> g.getLocalId().equals(gradeId))
            .findFirst()
            .orElseThrow();
        return localizedStringToToLocalizedText(grade.getAbbreviation());
    }

    public Person sisuPrivatePersonToPerson(Private_personQueryResponse privatePersonQueryResponse) {
        if (privatePersonQueryResponse.getData() != null) {
            Person person = new Person();
            person.studentNumber = privatePersonQueryResponse.private_person().getStudentNumber();
            person.teacherNumber = privatePersonQueryResponse.private_person().getEmployeeNumber();
            return person;
        }
        return null;
    }

    private Teacher sisuAcceptorPersonToTeacher(AcceptorPersonTO acceptorPerson) {
        Teacher teacher = new Teacher();
        PublicPersonTO person = acceptorPerson.getPerson();
        String firstName = (person != null && person.getFirstName() != null) ? person.getFirstName() : "";
        String lastName = (person != null && person.getLastName() != null) ? person.getLastName() : "";
        teacher.name = String.format("%s %s", firstName, lastName);
        return teacher;
    }

    public List<TeacherCourse> sisuCURSearchResultToTeacherCourseList(
        Authenticated_course_unit_realisation_searchQueryResponse curResult) {
        if (curResult != null && curResult.authenticated_course_unit_realisation_search() != null) {
            return curResult.authenticated_course_unit_realisation_search().stream()
                .map(FunctionHelper.logAndIgnoreExceptions(this::sisuCurToTeacherCourse))
                .collect(Collectors.filtering(Objects::nonNull, Collectors.toList()));
        }
        if (curResult != null && curResult.hasErrors()) {
            curResult.getErrors().forEach(e -> logger.error(e.toString()));
        }
        return List.of();
    }

    public TeacherCourse sisuCurToTeacherCourse(CourseUnitRealisationTO cur) {
        TeacherCourse tc = new TeacherCourse();
        tc.realisationId = cur.getId();
        tc.startDate = sisuDateStringToLocalDate(cur.getActivityPeriod().getStartDate());
        // https://sis-helsinki-test.funidata.fi/kori/docs/index.html#_localdaterange
        tc.endDate = sisuDateStringToLocalDate(cur.getActivityPeriod().getEndDate()).minusDays(1);
        tc.isCancelled = "CANCELLED".equals(cur.getFlowState());
        if (cur.getName() != null) {
            tc.name = localizedStringToToLocalizedText(cur.getName());
            tc.realisationName = localizedStringToToLocalizedText(cur.getName());
        }
        if (cur.getCourseUnits() != null && !cur.getCourseUnits().isEmpty()) {
            tc.learningOpportunityId = cur.getCourseUnits().get(0).getCode();
        }
        tc.organisations = cur.getOrganisations().stream()
            .filter(curOrg -> curOrg.getOrganisation() != null)
            .map(curOrg -> {
                Organisation org = new Organisation();
                org.code = curOrg.getOrganisation().getCode();
                org.name = localizedStringToToLocalizedText(curOrg.getOrganisation().getName());
                return org;
            }).collect(Collectors.toList());
        tc.realisationTypeCode = SISU_CUR_TYPE_TO_TYPE_CODE.getOrDefault(cur.getCourseUnitRealisationTypeUrn(), DEFAULT_CUR_TYPE_CODE);
        return tc;
    }

    private LocalDateTime sisuDateStringToLocalDate(String sisuDateString) {
        return LocalDate.parse(sisuDateString).atStartOfDay();
    }

    private List<LocalizedText> localizedStringToToLocalizedText(LocalizedStringTO name) {
        return List.of(
            new LocalizedText(StudyRegistryLocale.FI, name.getFi()),
            new LocalizedText(StudyRegistryLocale.SV, name.getSv()),
            new LocalizedText(StudyRegistryLocale.EN, name.getEn())
        );
    }

    public List<Event> sisuCurSearchResultToEvents(Authenticated_course_unit_realisation_searchQueryResponse curSearchResult, String teacherNumber) {
        if (curSearchResult != null && curSearchResult.authenticated_course_unit_realisation_search() != null) {
            return curSearchResult.authenticated_course_unit_realisation_search().stream()
                .map(FunctionHelper.logAndIgnoreExceptions(cur -> sisuCurToTeacherEvents(cur, teacherNumber)))
                .flatMap(x -> x.stream())
                .collect(Collectors.filtering(Objects::nonNull, Collectors.toList()));
        }

        if (curSearchResult != null && curSearchResult.hasErrors()) {
            curSearchResult.getErrors().forEach(e -> logger.error(e.toString()));
        }

        return List.of();
    }

    private LocalDateTime parseDateTime(String datetime) {
        return LocalDateTime.parse(datetime, sisuDateTimeFormatter);
    }

    private Event sisuEventToEvent(CourseUnitRealisationTO cur, StudySubGroupTO ssg, StudyEventTO se, StudyEventRealisationTO sisuEvent) {
        Event event = new Event();
        event.realisationId = cur.getId();
        event.realisationRootName = localizedStringToToLocalizedText(cur.getName());
        event.typeCode = SISU_CUR_TYPE_TO_TYPE_CODE.getOrDefault(cur.getCourseUnitRealisationTypeUrn(), DEFAULT_CUR_TYPE_CODE);

        if (ssg.getName() != null) {
            event.realisationName = localizedStringToToLocalizedText(ssg.getName());
        } else {
            event.realisationName = event.realisationRootName;
        }

        event.startDate = parseDateTime(sisuEvent.getStart());
        event.endDate = parseDateTime(sisuEvent.getEnd());
        event.isCancelled = sisuEvent.getCancelled();
        Optional.ofNullable(se.getLocations()).stream()
            .flatMap(List::stream)
            .findFirst()
            .ifPresent(loc -> {
                if (loc.getName() != null) {
                    event.roomName = loc.getName().getFi();
                }
                if (loc.getBuilding() != null) {
                    event.buildingStreet = loc.getBuilding().getAddress().getStreetAddress();
                    event.buildingZipCode = loc.getBuilding().getAddress().getPostalCode();
                }
            });

        Optional.ofNullable(se.getOverrides()).stream()
            .flatMap(List::stream)
            .filter(override -> override.getEventDate().equals(event.startDate.toLocalDate().toString()))
            .findFirst()
            .ifPresent(override -> {
                if (override.getNotice() != null && StringUtils.isNotBlank(override.getNotice().getFi())) {
                    event.optimeExtras = parseOptimeExtras(override.getNotice().getFi());
                }
                if (override.getIrregularLocations() != null) {
                    setEventLocation(event, override.getIrregularLocations());
                }
            }
        );
        return event;
    }

    public List<Event> sisuCurToTeacherEvents(CourseUnitRealisationTO cur, String teacherNumber) {
        return cur.getStudyGroupSets().stream()
            .map(sgs -> sgs.getStudySubGroups().stream()
                .filter(ssg -> ssg.getTeacherIds().contains(teacherNumber))
                .map(ssg -> ssg.getStudyEvents().stream()
                    .map(se -> se.getEvents().stream()
                        .filter(sisuEvent -> !sisuEvent.getExcluded())
                        .map(FunctionHelper.logAndIgnoreExceptions(sisuEvent -> sisuEventToEvent(cur, ssg, se, sisuEvent)))
                    ).filter(Objects::nonNull)
                )
            ).flatMap(Function.identity()).flatMap(Function.identity()).flatMap(Function.identity()).collect(Collectors.toList());
    }

    /**
     * Parses optime extras from zero width space (u200b) separated string.
     *
     * <p>Optime has three additional data fields for study events: OtherNotice, RoomNotice and StaffNotice.
     * These fields are not localized nor formatted, but may contain line feeds.
     * The data transfer to Sisu puts these three Optime fields, in the order listed above, into ONE field in Sisu.
     * Separated by a zero width space character '\u200B'.
     * The Sisu field is localized, and the same data gets copied for all languages by the data transfer.</p>
     *
     * @param extras Zero width space separated string
     * @return optime extras parsed from input string
     */
    public static OptimeExtras parseOptimeExtras(String extras) {
        OptimeExtras oe = new OptimeExtras();
        if (StringUtils.isBlank(extras)) {
            return oe;
        }

        List<String> split = Splitter.on('\u200b').splitToList(extras);
        if (split.size() == 3) {
            oe.otherNotes = split.get(0).trim();
            oe.roomNotes = split.get(1).trim();
            oe.staffNotes = split.get(2).trim();
        }
        return oe;
    }

    private void setEventLocation(Event event, List<LocationTO> locations) {
        Optional<LocationTO> loc = locations.stream().findFirst();
        loc.ifPresent(location -> {
            event.roomName = location.getName() != null ? location.getName().getFi() : null;
            if (location.getBuilding() != null && location.getBuilding().getAddress() != null) {
                event.buildingStreet = location.getBuilding().getAddress().getStreetAddress();
                event.buildingZipCode = location.getBuilding().getAddress().getPostalCode();
            }
        });
    }

}
