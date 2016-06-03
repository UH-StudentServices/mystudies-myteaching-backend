package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto.CourseMaterialType.COURSE_PAGE;
import static fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto.CourseMaterialType.MOODLE;
import static fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto.CourseMaterialType.WIKI;

@Component
public class CourseMaterialDtoFactory {
    private final CoursePageUriBuilder coursePageUriBuilder;

    @Autowired
    public CourseMaterialDtoFactory(CoursePageUriBuilder coursePageUriBuilder) {
        this.coursePageUriBuilder = coursePageUriBuilder;
    }

    public CourseMaterialDto fromCoursePage(CoursePageCourseImplementation coursePage) {
        if(coursePage == null) {
            return null;
        } else if(coursePage.moodleUrl != null) {
            return new CourseMaterialDto(coursePage.moodleUrl, MOODLE);
        } else if(coursePage.wikiUrl != null) {
            return new CourseMaterialDto(coursePage.wikiUrl, WIKI);
        } else if(coursePage.hasMaterial && coursePage.url != null) {
            return new CourseMaterialDto(coursePageUriBuilder.getMaterialUri(coursePage), COURSE_PAGE);
        } else {
            return null;
        }
    }
}
