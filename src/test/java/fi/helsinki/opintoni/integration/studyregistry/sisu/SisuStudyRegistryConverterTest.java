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

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedStringTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyGroupSetTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudySubGroupTO;

public class SisuStudyRegistryConverterTest {

    private static final String HY_HLO_ID = "hy-hlo-1";
    private static final String HY_HLO_ID2 = "hy-hlo-2";

    @Test
    public void thatMinimalSisuResponseIsConvertedToCourses() throws Exception {
        SisuStudyRegistryConverter converter = new SisuStudyRegistryConverter();
        List<TeacherCourse> courses = converter.sisuCURSearchResultToTeacherCourseList(getResponse());
        assertEquals(1, courses.size());
    }

    @Test
    public void thatMinimalSisuResponseIsConvertedToEvents() throws Exception {
        SisuStudyRegistryConverter converter = new SisuStudyRegistryConverter();
        List<Event> events = converter.sisuCurSearchResultToEvents(getResponse(), HY_HLO_ID);
        assertEquals(10, events.size());
    }

    private Authenticated_course_unit_realisation_searchQueryResponse getResponse() {
        CourseUnitRealisationTO cur = CourseUnitRealisationTO.builder()
            .setId("hy-CUR-1234")
            .setActivityPeriod(getPeriod("2020-01-01", "2020-04-04"))
            .setCourseUnits(List.of(getCourseUnit("code")))
            .setStudyGroupSets(List.of(getStudyGroupSet()))
            .setOrganisations(List.of())
            .setName(name("cur name fi", "cur name en", "cur name sv"))
            .build();
        Authenticated_course_unit_realisation_searchQueryResponse response = new Authenticated_course_unit_realisation_searchQueryResponse();
        response.setData(Map.of("authenticated_course_unit_realisation_search", List.of(cur)));
        return response;
    }

    private StudyGroupSetTO getStudyGroupSet() {
        return StudyGroupSetTO.builder().setName(name("sgs name fi", "sgs name en", "sgs name sv"))
                .setStudySubGroups(List.of(
                    getStudySubGroup(HY_HLO_ID),
                    getStudySubGroup(HY_HLO_ID2)
                )).build();
    }

    private LocalizedStringTO name(String fi, String en, String sv) {
        return LocalizedStringTO.builder().setFi(fi).setEn(en).setSv(sv).build();
    }

    private StudySubGroupTO getStudySubGroup(String hloId) {
        return StudySubGroupTO.builder().setName(name("ssg name fi", "ssg name en", "ssg name sv"))
            .setStudyEvents(List.of(studyEvent()))
            .setTeacherIds(List.of(hloId))
            .build();
    }

    private StudyEventTO studyEvent() {
        return StudyEventTO.builder()
            .setName(name("study event name fi", "study event name en", "study event name sv"))
            .setEvents(events())
            .build();
    }

    private List<StudyEventRealisationTO> events() {
        List<StudyEventRealisationTO> events = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            LocalDateTime start = now.plusWeeks(i);
            events.add(StudyEventRealisationTO.builder()
                .setExcluded(false)
                .setStart(start.format(DateTimeFormatter.ISO_DATE_TIME))
                .setEnd(start.plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME))
                .setCancelled(false)
                .build());
        }
        return events;
    }

    private DatePeriodTO getPeriod(String startDate, String endDate) {
        return DatePeriodTO.builder()
            .setStartDate(startDate)
            .setEndDate(endDate)
            .build();
    }

    private CourseUnitTO getCourseUnit(String code) {
        return CourseUnitTO.builder()
            .setCode(code)
            .build();
    }

}
