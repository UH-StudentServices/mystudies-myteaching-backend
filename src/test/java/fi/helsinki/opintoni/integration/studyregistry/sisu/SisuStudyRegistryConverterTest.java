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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodTO;

public class SisuStudyRegistryConverterTest {

    @Test
    public void thatMinimalSisuResponseIsConverted() throws Exception {
        SisuStudyRegistryConverter converter = new SisuStudyRegistryConverter();
        List<TeacherCourse> courses = converter.sisuCURSearchResultToTeacherCourseList(getEventsResponse());
        assertEquals(1, courses.size());

    }

    private Authenticated_course_unit_realisation_searchQueryResponse getEventsResponse() {
        CourseUnitRealisationTO cur = new CourseUnitRealisationTO();
        cur.setId("hy-CUR-1234");
        cur.setActivityPeriod(getPeriod("2020-01-01", "2020-04-04"));
        cur.setCourseUnits(List.of(getCourseUnit("code")));
        cur.setOrganisations(List.of());
        Authenticated_course_unit_realisation_searchQueryResponse response = new Authenticated_course_unit_realisation_searchQueryResponse();
        response.setData(Map.of("authenticated_course_unit_realisation_search", List.of(cur)));
        return response;
    }

    private DatePeriodTO getPeriod(String startDate, String endDate) {
        DatePeriodTO activityPeriod = new DatePeriodTO();
        activityPeriod.setStartDate(startDate);
        activityPeriod.setEndDate(endDate);
        return activityPeriod;
    }

    private CourseUnitTO getCourseUnit(String code) {
        CourseUnitTO cu = new CourseUnitTO();
        cu.setCode(code);
        return cu;
    }

}
