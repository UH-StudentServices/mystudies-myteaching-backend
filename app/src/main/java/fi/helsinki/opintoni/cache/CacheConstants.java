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

package fi.helsinki.opintoni.cache;

import com.google.common.collect.ImmutableList;

public class CacheConstants {

    public static final String STUDENT_EVENTS = "studentEvents";
    public static final String STUDENT_ENROLLMENTS = "studentEnrollments";

    public static final String TEACHER_EVENTS = "teacherEvents";
    public static final String TEACHER_COURSES = "teacherCourses";

    public static final String COURSE_PAGE = "coursePage";
    public static final String COURSE_PAGE_ONE_OFF_EVENTS = "coursePageOneOffEvents";

    public static final String BUILDINGS = "buildings";
    public static final String COURSE_UNIT_REALISATIONS = "courseUniRealisations";

    public static final String STUDENT_NEWS = "studentNews";
    public static final String TEACHER_NEWS = "teacherNews";
    public static final String OPEN_UNIVERSITY_NEWS = "openUniversityNews";

    public static final String IS_OPEN_UNIVERSITY_STUDENT = "isOpenUniversityStudent";
    public static final String IS_OPEN_UNIVERSITY_TEACHER = "isOpenUniversityTeacher";

    public static final String EUROPE_HELSINKI_TIMEZONE = "europeHelsinkiTimeZone";

    public static final String FEEDS = "feeds";

    public static final ImmutableList<String> cacheNames = ImmutableList.<String>builder()
        .add(STUDENT_EVENTS)
        .add(STUDENT_ENROLLMENTS)
        .add(TEACHER_EVENTS)
        .add(TEACHER_COURSES)
        .add(COURSE_PAGE)
        .add(COURSE_PAGE_ONE_OFF_EVENTS)
        .add(BUILDINGS)
        .add(COURSE_UNIT_REALISATIONS)
        .add(STUDENT_NEWS)
        .add(TEACHER_NEWS)
        .add(OPEN_UNIVERSITY_NEWS)
        .add(IS_OPEN_UNIVERSITY_STUDENT)
        .add(IS_OPEN_UNIVERSITY_TEACHER)
        .add(EUROPE_HELSINKI_TIMEZONE)
        .add(FEEDS)
        .build();
}
