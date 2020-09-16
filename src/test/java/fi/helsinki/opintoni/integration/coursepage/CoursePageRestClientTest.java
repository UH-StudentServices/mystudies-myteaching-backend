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

package fi.helsinki.opintoni.integration.coursepage;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CoursePageRestClientTest extends SpringTest {

    private static final String STRIPPED_CUR_ID = IntegrationUtil.stripKnownSisuCurPrefixes(TEACHER_COURSE_REALISATION_ID);

    @Autowired
    private CoursePageClient coursePageRestClient;

    private static final Locale EN = Locale.ENGLISH;
    private static final Locale FI = new Locale("fi");
    private static final Locale SV = new Locale("sv");
    private static final String EMPTY_COURSE_RESPONSE = "course_empty.json";

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    @Test
    public void thatImageUriIsReturned() {
        defaultTeacherRequestChain().courseImplementationWithLocale(EN);

        CoursePageCourseImplementation coursePage = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN);

        assertThat(coursePage.imageUrl).isEqualTo("http://dev.student.helsinki.fi/image");
    }

    private void expectCourseImplementationWithLocale(Locale locale) {
        defaultTeacherRequestChain().courseImplementationWithLocale(locale);
        coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);
    }

    @Test
    public void thatEnglishLocaleImplementationIsRequested() {
        expectCourseImplementationWithLocale(EN);
    }

    @Test
    public void thatFinnishLocaleImplementationIsRequested() {
        expectCourseImplementationWithLocale(FI);
    }

    @Test
    public void thatSwedishLocaleImplementationIsRequested() {
        expectCourseImplementationWithLocale(SV);
    }

    @Test
    public void thatEmptyImageUriIsReturnedWhenCoursePageHasNoImageUri() {
        defaultTeacherRequestChain()
            .courseImplementation(STRIPPED_CUR_ID, "course_without_image.json", EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isEqualTo("");
    }

    @Test
    public void thatNullImageUriIsReturnedWhenCoursePageDoesNotExist() {
        defaultTeacherRequestChain().courseImplementation(STRIPPED_CUR_ID, EMPTY_COURSE_RESPONSE, EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isNull();
    }

    @Test
    public void thatNullImageUriIsReturnedWhenCoursePageReturns404() {
        coursePageServer.expectCourseImplementationRequestAndReturnStatus(STRIPPED_CUR_ID, EN, HttpStatus.NOT_FOUND);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isNull();
    }

    @Test
    public void thatMoodleUrlIsReturned() {
        defaultTeacherRequestChain()
            .courseImplementation(STRIPPED_CUR_ID, "course_with_moodle_url.json", EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).moodleUrl).isEqualTo("http://moodle.helsinki.fi");
    }

    @Test
    public void thatCoursePageCourseImplementationWithCourseIdIsReturnedWhenCoursePageHasNoContent() {
        final int expectedCourseRealisationId = Integer.parseInt(TEACHER_COURSE_REALISATION_ID.substring(7));

        defaultTeacherRequestChain().courseImplementation(STRIPPED_CUR_ID, EMPTY_COURSE_RESPONSE, EN);

        CoursePageCourseImplementation course = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN);

        assertThat(course.courseImplementationId).isEqualTo(expectedCourseRealisationId);
    }
}
