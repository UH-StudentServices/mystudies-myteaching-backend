package fi.helsinki.opintoni.task;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CourseImplementationCacheBusterTest extends SpringTest {
    @Autowired
    private CoursePageClient coursePageRestClient;

    @Autowired
    private CourseImplementationCacheBuster courseImplementationCacheBuster;

    @Test
    public void thatCourseImplementationCacheIsBusted() {
        defaultTeacherRequestChain().defaultCourseImplementation();
        expectCourseImplementationChangesRequest();
        defaultTeacherRequestChain().defaultCourseImplementation();

        CoursePageCourseImplementation implementations = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID);

        courseImplementationCacheBuster.checkForUpdatedCourseImplementations(Instant.now().getEpochSecond());

        CoursePageCourseImplementation implementationsAfterCacheBust =
            coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID);

        assertThat(implementations).isNotSameAs(implementationsAfterCacheBust);
    }
}
