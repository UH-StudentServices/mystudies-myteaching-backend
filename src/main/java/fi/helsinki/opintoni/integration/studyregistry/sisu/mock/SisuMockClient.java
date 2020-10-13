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

package fi.helsinki.opintoni.integration.studyregistry.sisu.mock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AttainmentTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.EventOverrideTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeScaleTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedStringTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyGroupSetTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudySubGroupTO;

public class SisuMockClient implements SisuClient {

    private static final String HY_HLO_ID = "hy-hlo-1000";
    private static final String CUR_ID_WITH_CMS_DATA = "hy-CUR-234567891"; // @see CourseCmsMockClient
    private static final String CUR_ID_PAST = "hy-CUR-234567892";
    private static final String CUR_ID_FUTURE = "hy-CUR-234567893";
    private static final String CUR_PAST_EXAM = "hy-CUR-234567894";
    private static final String CUR_FUTURE_EXAM = "hy-CUR-234567895";

    Map<String, String> curNames = Map.of(
        CUR_ID_PAST, "Frumulointi I",
        CUR_ID_WITH_CMS_DATA, "Formulointi II",
        CUR_ID_FUTURE, "Formulointi III",
        CUR_PAST_EXAM, "Mennyt tentti",
        CUR_FUTURE_EXAM, "Tuleva tentti"
    );

    @Override
    public Authenticated_course_unit_realisation_searchQueryResponse curSearch(String personId, LocalDate since) {
        LocalDate now = LocalDate.now();
        Authenticated_course_unit_realisation_searchQueryResponse response = new Authenticated_course_unit_realisation_searchQueryResponse();
        response.setData(Map.of("authenticated_course_unit_realisation_search", List.of(
            cur(now.minusMonths(4), CUR_ID_PAST, 10),
            cur(now, CUR_ID_WITH_CMS_DATA, 10),
            cur(now.plusYears(1), CUR_ID_FUTURE, 10),
            cur(now.minusWeeks(1), CUR_PAST_EXAM, 1),
            cur(now.plusWeeks(1), CUR_FUTURE_EXAM, 1)
            )));
        return response;
    }

    private CourseUnitRealisationTO cur(LocalDate start, String curId, int eventCount) {
        boolean isExam = eventCount == 1;
        return CourseUnitRealisationTO.builder()
            .setId(curId)
            .setActivityPeriod(getPeriod(start.toString(),
                isExam
                    ? start.plusDays(1).toString()
                    : start.plusMonths(3).toString()))
            .setCourseUnits(List.of(getCourseUnit("ABC123")))
            .setStudyGroupSets(List.of(getStudyGroupSet(start, eventCount)))
            .setOrganisations(List.of())
            .setName(name(curNames.get(curId)))
            .setCourseUnitRealisationTypeUrn(
                isExam
                    ? "urn:code:course-unit-realisation-type:exam-exam"
                    : "urn:code:course-unit-realisation-type:teaching-participation-lectures")
            .build();
    }

    private LocalizedStringTO name(String prefix) {
        return LocalizedStringTO.builder().setFi(prefix + " fi").setEn(prefix + " en").setSv(prefix + " sv").build();
    }

    private DatePeriodTO getPeriod(String startDate, String endDate) {
        return DatePeriodTO.builder()
            .setStartDate(startDate)
            .setEndDate(endDate)
            .build();
    }

    private StudySubGroupTO getStudySubGroup(LocalDate start, String hloId, int eventCount) {
        return StudySubGroupTO.builder().setName(name("SSG nimi Sisusta"))
            .setStudyEvents(List.of(studyEvent(start, eventCount)))
            .setTeacherIds(List.of(hloId))
            .setCancelled(false)
            .build();
    }

    private StudyEventTO studyEvent(LocalDate start, int eventCount) {
        return StudyEventTO.builder()
            .setName(name("Study event nimi Sisusta"))
            .setOverrides(List.of(EventOverrideTO.builder()
                .setEventDate(start.toString())
                .setNotice(LocalizedStringTO.builder()
                    .setFi("room notes sisusta\u200bstaff notes sisusta\u200bother notes sisusta").build()).build()))
                    .setLocations(List.of())
            .setEvents(events(start, eventCount))
            .build();
    }

    private List<StudyEventRealisationTO> events(LocalDate startDate, int eventCount) {
        return IntStream.range(0, eventCount).mapToObj(
            i -> StudyEventRealisationTO.builder()
                .setExcluded(false)
                .setStart(startDate.plusWeeks(i).atTime(8, 45).format(DateTimeFormatter.ISO_DATE_TIME))
                .setEnd(startDate.plusWeeks(i).atTime(9, 45).format(DateTimeFormatter.ISO_DATE_TIME))
                .setCancelled(false)
                .build()).collect(Collectors.toList());
    }

    @Override
    public Private_personQueryResponse getPrivatePerson(String personId) {
        Private_personQueryResponse r = new Private_personQueryResponse();
        PrivatePersonTO pp = new PrivatePersonTO();
        pp.setStudentNumber("010189791");
        pp.setEmployeeNumber("010540");
        r.setData(Map.of(personId, pp));
        return r;
    }

    @Override
    public Private_personQueryResponse getStudyAttainments(String personId) {
        AttainmentTO attainment = new AttainmentTO();
        attainment.setId("hy-opinto-126377006");
        attainment.setCredits(5.0);
        attainment.setAttainmentDate("2019-05-09");
        attainment.setGradeScale(getGradeScale());
        attainment.setCourseUnit(getCourseUnit("abc-123"));
        PrivatePersonTO pp = new PrivatePersonTO();
        pp.setAttainments(List.of(attainment));
        Private_personQueryResponse r = new Private_personQueryResponse();
        r.setData(Map.of(personId, pp));
        return r;
    }

    private CourseUnitTO getCourseUnit(String code) {
        CourseUnitTO courseUnit = new CourseUnitTO();
        courseUnit.setCode(code);
        courseUnit.setName(getLocalizedString("Integraalilaskenta", "Integral Calculus", "Integral kalkyl"));
        return courseUnit;
    }

    private StudyGroupSetTO getStudyGroupSet(LocalDate start, int eventCount) {
        return StudyGroupSetTO.builder().setName(name("Luennot"))
                .setStudySubGroups(List.of(
                    getStudySubGroup(start, HY_HLO_ID, eventCount)
                )).build();
    }

    private GradeScaleTO getGradeScale() {
        GradeScaleTO gradeScale = new GradeScaleTO();
        GradeTO grade0 = getGrade("0", "0", "0", "0");
        GradeTO grade1 = getGrade("1", "1", "1", "1");
        GradeTO grade2 = getGrade("2", "2", "2", "2");
        GradeTO grade3 = getGrade("3", "3", "3", "3");
        GradeTO grade4 = getGrade("4", "4", "4", "4");
        GradeTO grade5 = getGrade("5", "5", "5", "5");
        gradeScale.setGrades(List.of(grade0, grade1, grade2, grade3, grade4, grade5));
        return gradeScale;
    }

    private GradeTO getGrade(String id, String fi, String en, String sv) {
        GradeTO grade = new GradeTO();
        grade.setLocalId(id);
        grade.setAbbreviation(getLocalizedString(fi, en, sv));
        return grade;
    }

    private LocalizedStringTO getLocalizedString(String fi, String en, String sv) {
        LocalizedStringTO localizedString = new LocalizedStringTO();
        localizedString.setFi(fi);
        localizedString.setSv(sv);
        localizedString.setEn(en);
        return localizedString;
    }
}
