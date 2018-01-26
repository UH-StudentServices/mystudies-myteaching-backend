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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CoursePageRestClientTest extends SpringTest {

    @Autowired
    private CoursePageClient coursePageRestClient;

    private static final Locale EN = Locale.ENGLISH;
    private static final Locale FI = new Locale("fi");
    private static final Locale SV = new Locale("sv");

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
            .courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_without_image.json", EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isEqualTo("");
    }

    @Test
    public void thatNullImageUriIsReturnedWhenCoursePageDoesNotExist() {
        defaultTeacherRequestChain().courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_empty.json", EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isNull();
    }

    @Test
    public void thatNullImageUriIsReturnedWhenCoursePageReturns404() {
        coursePageServer.expectCourseImplementationRequestAndReturnStatus(TEACHER_COURSE_REALISATION_ID, EN, HttpStatus.NOT_FOUND);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).imageUrl).isNull();
    }

    @Test
    public void thatMoodleUrlIsReturned() {
        defaultTeacherRequestChain()
            .courseImplementation(TEACHER_COURSE_REALISATION_ID, "course_with_moodle_url.json", EN);

        assertThat(coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, EN).moodleUrl).isEqualTo("http://moodle.helsinki.fi");
    }


}
