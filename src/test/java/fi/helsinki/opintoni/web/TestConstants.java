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

package fi.helsinki.opintoni.web;

import fi.helsinki.opintoni.localization.Language;

import java.util.Locale;

public class TestConstants {

    public static final String STUDENT_NUMBER = "010189791";
    public static final String STUDENT_COURSE_REALISATION_ID = "123456789";
    public static final String STUDENT_PERSON_ID = "1001";

    public static final String EMPLOYEE_NUMBER = "010540";
    public static final String TEACHER_COURSE_REALISATION_ID = "99903629";

    public static final String EXAM_TEACHER_COURSE_REALISATION_ID = "99903628";
    public static final String POSITION_STUDYGROUP_TEACHER_COURSE_REALISATION_ID = "1234567";

    public static final Long PROFILE_ID = 2L;

    public static final Locale DEFAULT_USER_LOCALE = new Locale(Language.FI.getCode());
}
