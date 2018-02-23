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

    public static final String GUIDE_DEGREE_PROGRAMMES = "guideDegreeProgrammes";

    public static final String BUILDINGS = "buildings";
    public static final String COURSE_UNIT_REALISATION_TEACHERS = "courseUnitRealisationTeachers";
    public static final String LEARNING_OPPORTUNITIES = "learningOpportunities";

    public static final String STUDENT_NEWS = "studentNews";
    public static final String TEACHER_NEWS = "teacherNews";
    public static final String OPEN_UNIVERSITY_NEWS = "openUniversityNews";
    public static final String GUIDE_GENERAL_NEWS = "guideGeneralNews";
    public static final String GUIDE_PROGRAMME_NEWS = "guideProgrammeNews";

    public static final String IS_OPEN_UNIVERSITY_STUDENT = "isOpenUniversityStudent";
    public static final String IS_OPEN_UNIVERSITY_TEACHER = "isOpenUniversityTeacher";

    public static final String EUROPE_HELSINKI_TIMEZONE = "europeHelsinkiTimeZone";

    public static final String FEEDS = "feeds";

    public static final String SISU_COURSE_UNIT_REALISATION = "sisuCourseUnitRealisation";
    public static final String SISU_PERSON = "sisuPerson";
    public static final String SISU_LOCATION = "sisuLocation";
    public static final String SISU_BUILDING = "sisuBuilding";
    public static final String SISU_STUDY_EVENT = "sisuStudyEvent";
    public static final String SISU_ASSESSMENT = "sisuAssessment";
    public static final String SISU_ENROLMENTS = "sisuEnrolments";

    public static final ImmutableList<String> transientCacheNames = ImmutableList.<String>builder()
        .add(STUDENT_EVENTS)
        .add(STUDENT_ENROLLMENTS)
        .add(TEACHER_EVENTS)
        .add(TEACHER_COURSES)
        .add(GUIDE_DEGREE_PROGRAMMES)
        .add(BUILDINGS)
        .add(COURSE_UNIT_REALISATION_TEACHERS)
        .add(LEARNING_OPPORTUNITIES)
        .add(STUDENT_NEWS)
        .add(TEACHER_NEWS)
        .add(OPEN_UNIVERSITY_NEWS)
        .add(IS_OPEN_UNIVERSITY_STUDENT)
        .add(IS_OPEN_UNIVERSITY_TEACHER)
        .add(EUROPE_HELSINKI_TIMEZONE)
        .add(FEEDS)
        .add(GUIDE_GENERAL_NEWS)
        .add(GUIDE_PROGRAMME_NEWS)
        .add(SISU_COURSE_UNIT_REALISATION)
        .add(SISU_PERSON)
        .add(SISU_LOCATION)
        .add(SISU_BUILDING)
        .add(SISU_STUDY_EVENT)
        .add(SISU_ASSESSMENT)
        .add(SISU_ENROLMENTS)
        .build();

    public static final ImmutableList<String> persistentCacheNames = ImmutableList.<String>builder()
        .add(COURSE_PAGE)
        .build();
}
