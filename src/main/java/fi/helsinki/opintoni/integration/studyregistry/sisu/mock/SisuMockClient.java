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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AttainmentTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationOrganisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeScaleTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedStringTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.OrganisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.RangeTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyGroupSetTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudySubGroupTO;

public class SisuMockClient implements SisuClient {

    @Override
    public Authenticated_course_unit_realisation_searchQueryResponse curSearch(String personId, LocalDate since) {
        CourseUnitRealisationTO cur = new CourseUnitRealisationTO();
        cur.setCourseUnits(List.of(getCourseUnit("abc")));
        cur.setId("hy-cur-1");
        cur.setActivityPeriod(getActivityPeriod());
        cur.setOrganisations(getCourseUnitRealisationOrganisations("org-1"));
        cur.setStudyGroupSets(getStudyGroupSets(personId));
        Authenticated_course_unit_realisation_searchQueryResponse r = new Authenticated_course_unit_realisation_searchQueryResponse();
        r.setData(Map.of("authenticated_course_unit_realisation_search", List.of(cur)));
        return r;
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

    private DatePeriodTO getActivityPeriod() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() > 7 ? 8 : 1, 1);
        LocalDate endDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() > 7 ? 12 : 7, 31);
        return new DatePeriodTO(endDate.toString(), startDate.toString());
    }

    private List<CourseUnitRealisationOrganisationTO> getCourseUnitRealisationOrganisations(String... ids) {
        return Arrays.asList(ids).stream().map(id -> {
            return new CourseUnitRealisationOrganisationTO(
                id,
                new OrganisationTO(id, id + "-code", null,
                        new LocalizedStringTO("TestiOrg" + id, "TestOrg" + id, "TestOrg1" + id, null, null, null),
                        new LocalizedStringTO("Testi Organisaatio", "Test Organisation", "Testi Organisaatio", null,
                                null, null),
                        List.of(), List.of(), "parent-org-" + id, null, "hy-test-org-" + id, null, null, null, "ACTIVE"),
                "urn:", 100D, new DatePeriodTO("2050-01-30", "2010-08-01")
            );
        }).collect(Collectors.toList());
    }

    private List<StudyGroupSetTO> getStudyGroupSets(String personId) {
        return List.of(
            new StudyGroupSetTO(
                "sgs-1",
                new LocalizedStringTO("Luennot", "Lectures", "Luennot (sv)", null, null, null),
                List.of(new StudySubGroupTO(false, "ACTIVE", "ssg-1",
                    new LocalizedStringTO("Ryhmä 1", "Group 1", "Grupp 1", null, null, null), 20,
                    List.of("se-1", "se-2", "se-3"), List.of(), List.of("hy-teacher-person-1", personId), List.of())),
                new RangeTO(0, 30)
            )
        );
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
