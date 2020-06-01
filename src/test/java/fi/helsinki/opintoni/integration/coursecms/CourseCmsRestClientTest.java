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

package fi.helsinki.opintoni.integration.coursecms;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static fi.helsinki.opintoni.integration.IntegrationUtil.getSisuCourseUnitRealisationId;
import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CourseCmsRestClientTest extends SpringTest {

    private static final Locale EN = Locale.ENGLISH;
    private static final Locale FI = new Locale("fi");
    private static final Locale SV = new Locale("sv");

    @Autowired
    private CourseCmsClient courseCmsClient;

    public void expectCourseUnitRealisationWithLocale(Locale locale) {
        defaultTeacherRequestChain().courseCmsCourseUnitRealisation(locale);
        courseCmsClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);
    }

    @Test
    public void thatEnglishLocaleCourseUnitRealisationIsRequested() {
        expectCourseUnitRealisationWithLocale(EN);
    }

    @Test
    public void thatFinnishLocaleCourseUnitRealisationIsRequested() {
        expectCourseUnitRealisationWithLocale(FI);
    }

    @Test
    public void thatSwedishLocaleCourseUnitRealisationIsRequested() {
        expectCourseUnitRealisationWithLocale(SV);
    }

    @Test
    public void thatEmptyCourseUnitRealisationIsReturnedIfCmsDataDoesNotExist() {
        defaultTeacherRequestChain().courseCmsCourseUnitRealisation(TEACHER_COURSE_REALISATION_ID, "course_empty.json", FI);
        CourseCmsCourseUnitRealisation coursePage = courseCmsClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, FI);
        assertThat(coursePage.courseUnitRealisationId).isEqualTo(getSisuCourseUnitRealisationId(TEACHER_COURSE_REALISATION_ID));
        assertThat(coursePage.name).isNull();
        assertThat(coursePage.courseImage).isNull();
        assertThat(coursePage.moodleLink).isNull();
    }

    @Test
    public void thatCourseImageUrlIsReturned() {
        defaultTeacherRequestChain().courseCmsCourseUnitRealisation(TEACHER_COURSE_REALISATION_ID, "course.json", FI);
        CourseCmsCourseUnitRealisation coursePage = courseCmsClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, FI);
        assertThat(coursePage.courseImage.uri.url).isEqualTo("/sites/default/files/2020-02/IMG_0255.JPG");
    }

    @Test
    public void thatMoodleLinkIsReturned() {
        defaultTeacherRequestChain().courseCmsCourseUnitRealisation(TEACHER_COURSE_REALISATION_ID, "course.json", FI);
        CourseCmsCourseUnitRealisation coursePage = courseCmsClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, FI);
        assertThat(coursePage.moodleLink.uri).isEqualTo("https://moodle.helsinki.fi/course/view.php?id=29433");
    }

    @Test
    public void thatCourseWithoutImageIsReturnedWithoutImageUrl() {
        defaultTeacherRequestChain().courseCmsCourseUnitRealisation(TEACHER_COURSE_REALISATION_ID, "course_without_image.json", FI);
        CourseCmsCourseUnitRealisation coursePage = courseCmsClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, FI);
        assertThat(coursePage.courseImage.uri).isNull();
    }
}
