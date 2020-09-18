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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.dto.profile.CourseMaterialDto;
import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnrollmentResourceGetTeacherEventsTest extends SpringTest {

    private static final String CUR_ID = "hy-CUR-99903629";

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    @MockBean
    CoursePageClient mockCoursePageClient;

    @MockBean
    CourseCmsClient mockCourseCmsClient;

    @MockBean
    SotkaClient mockSotkaClient;

    @Test
    public void thatTeacherEventsAreReturned() throws Exception {
        final LocalDateTime COURSE_START = LocalDateTime.of(2015, 8, 13, 10, 0);

        when(mockSisuStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class))).thenReturn(List.of(course("xyz", COURSE_START)));
        when(mockSisuStudyRegistry.getTeacherEvents(anyString())).thenReturn(List.of(event(COURSE_START)));
        when(mockCoursePageClient.getCoursePage(anyString(), any(Locale.class))).thenReturn(coursePageCourseImplementation());

        mockMvc.perform(get("/api/private/v1/teachers/enrollments/events")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].realisationId").value(CUR_ID))
            .andExpect(jsonPath("$[0].endDate").isArray())
            .andExpect(jsonPath("$[0].endDate[0]").value(2015))
            .andExpect(jsonPath("$[0].endDate[1]").value(8))
            .andExpect(jsonPath("$[0].endDate[2]").value(13))
            .andExpect(jsonPath("$[0].endDate[3]").value(14))
            .andExpect(jsonPath("$[0].endDate[4]").value(0))
            .andExpect(jsonPath("$[0].startDate").isArray())
            .andExpect(jsonPath("$[0].startDate[0]").value(2015))
            .andExpect(jsonPath("$[0].startDate[1]").value(8))
            .andExpect(jsonPath("$[0].startDate[2]").value(13))
            .andExpect(jsonPath("$[0].startDate[3]").value(10))
            .andExpect(jsonPath("$[0].startDate[4]").value(0))
            .andExpect(jsonPath("$[0].title").value("Formulointi III"))
            .andExpect(jsonPath("$[0].type").value(EventDto.Type.DEFAULT.name()))
            .andExpect(jsonPath("$[0].source").value(EventDto.Source.STUDY_REGISTRY.name()))
            .andExpect(jsonPath("$[0].courseTitle").value("Animal Biotechnology B (KEL/KEBIOT230)"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialUri")
                .value("https://dev.student.helsinki.fi/tvt?group-imp-material"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialType")
                .value(CourseMaterialDto.CourseMaterialType.COURSE_PAGE.toString()))
            .andExpect(jsonPath("$[0].locations[0].locationString").value("F24 Fabianinkatu 24, Sali 531, Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].roomName").value("F24 Fabianinkatu 24, Sali 531"))
            .andExpect(jsonPath("$[0].locations[0].streetAddress").value("Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].zipCode").value("00790"))
            .andExpect(jsonPath("$[0].hasMaterial").value(true))
            .andExpect(jsonPath("$[0].isHidden").value(false));
    }

    private CoursePageCourseImplementation coursePageCourseImplementation() {
        CoursePageCourseImplementation coursePage = new CoursePageCourseImplementation();
        coursePage.title = "Animal Biotechnology B (KEL/KEBIOT230)";
        coursePage.hasMaterial = true;
        coursePage.url = "https://dev.student.helsinki.fi/tvt";
        coursePage.courseImplementationId = Integer.parseInt(IntegrationUtil.stripPossibleSisuOodiCurPrefix(CUR_ID));
        return coursePage;
    }

    private Event event(LocalDateTime startDateTime) {
        Event event = new Event();
        event.realisationId = CUR_ID;
        event.realisationName = List.of(new LocalizedText(StudyRegistryLocale.FI, "Formulointi III"));
        event.endDate = startDateTime.plusHours(4);
        event.startDate = startDateTime;
        event.buildingStreet = "Viikinkaari 11";
        event.roomName = "F24 Fabianinkatu 24, Sali 531";
        event.buildingZipCode = "00790";
        return event;
    }

    private TeacherCourse course(String code, LocalDateTime startDateTime) {
        TeacherCourse course = new TeacherCourse();
        course.learningOpportunityId = code;
        course.realisationId = CUR_ID;
        course.rootId = CUR_ID;
        course.startDate = startDateTime;
        course.endDate = startDateTime.plusWeeks(1);
        course.realisationName = List.of(new LocalizedText(StudyRegistryLocale.FI, "Formulointi III"));
        return course;
    }

    @Test
    public void thatTeacherEventsAreEnrichedWithNewCoursePageDataForCoursesStartingAfterCutOffDate() throws Exception {
        final LocalDateTime COURSE_START = LocalDateTime.of(2020, 11, 13, 10, 0);
        when(mockSisuStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class))).thenReturn(List.of(course("xyz", COURSE_START)));
        when(mockSisuStudyRegistry.getTeacherEvents(anyString())).thenReturn(List.of(event(COURSE_START)));
        when(mockCourseCmsClient.getCoursePage(anyString(), any(Locale.class))).thenReturn(coursePageCMSImplementation());

        mockMvc.perform(get("/api/private/v1/teachers/enrollments/events")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Formulointi III"))
            .andExpect(jsonPath("$[0].courseUri").value("https://studies-qa.it.helsinki.fi/Shibboleth.sso/Login?target=https%3A%2F%2Fstudies-qa.it.helsinki.fi%2Fopintotarjonta%2Fcur%2Fhy-CUR-99903629"));

        verify(mockCourseCmsClient, times(1)).getCoursePage(CUR_ID, new Locale("fi"));
    }

    private CourseCmsCourseUnitRealisation coursePageCMSImplementation() {
        CourseCmsCourseUnitRealisation coursePage = new CourseCmsCourseUnitRealisation();
        coursePage.courseUnitRealisationId = CUR_ID;
        return coursePage;
    }

}
