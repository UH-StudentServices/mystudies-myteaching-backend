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

package fi.helsinki.opintoni.util;

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.service.converter.CoursePageUriLocalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoursePageUriBuilder {

    private final ImmutableMap<String, String> pathParameterByNotificationType;
    private final CoursePageUriLocalizer coursePageUriLocalizer;
    private final AppConfiguration appConfiguration;

    @Autowired
    public CoursePageUriBuilder(CoursePageUriLocalizer coursePageUriLocalizer, AppConfiguration appConfiguration) {
        this.coursePageUriLocalizer = coursePageUriLocalizer;
        this.appConfiguration = appConfiguration;
        pathParameterByNotificationType = ImmutableMap.<String, String>builder()
            .put("course_implementation_comment_create", "group-course-messages")
            .put("course_implementation_comment_remove", "group-course-messages")
            .put("course_implementation_comment_update", "group-course-messages")
            .put("course_implementation_event_create", "group-timetable")
            .put("course_implementation_event_remove", "group-timetable")
            .put("course_implementation_event_update", "group-timetable")
            .put("course_implementation_material_create", "group-imp-material")
            .put("course_implementation_material_description_update", "group-imp-material")
            .put("course_implementation_material_remove", "group-imp-material")
            .put("course_implementation_material_update", "group-imp-material")
            .put("course_implementation_teacher_section_create", "group-custom-section")
            .put("course_implementation_teacher_section_remove", "group-custom-section")
            .put("course_implementation_teacher_section_update", "group-custom-section")
            .put("course_implementation_course_conduct_create", "group-conduct-of-course")
            .put("course_implementation_course_conduct_remove", "group-conduct-of-course")
            .put("course_implementation_course_conduct_update", "group-conduct-of-course")
            .put("course_implementation_course_overview_accordion_target", "group-description-objectives")
            .put("course_implementation_task_create", "group-course-task-material")
            .put("course_implementation_task_remove", "group-course-task-material")
            .put("course_implementation_task_update", "group-course-task-material")
            .build();

    }

    public String getNotificationUriByNotificationType(String localizedCoursePageUri, String notificationType) {
        return localizedCoursePageUri + "?" + pathParameterByNotificationType.get(notificationType);
    }

    public String getMaterialUri(CoursePageCourseImplementation coursePage) {
        return coursePage.url != null ? getLocalizedUri(coursePage) + "?group-imp-material" : null;
    }

    public String getLocalizedUri(CoursePageCourseImplementation coursePage) {
        return coursePage.url != null ? coursePageUriLocalizer.localize(coursePage.url) : null;
    }

    public String getImageUri(CoursePageCourseImplementation coursePage) {
        return (coursePage != null && coursePage.imageUrl != null)
            ? coursePage.imageUrl
            : appConfiguration.get("coursePage.defaultCourseImageUri");
    }
}
