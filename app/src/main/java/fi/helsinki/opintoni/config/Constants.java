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

package fi.helsinki.opintoni.config;

public final class Constants {

    private Constants() {
    }

    public static final String SPRING_PROFILE_LOCAL_DEVELOPMENT = "local-dev";
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_QA = "qa";
    public static final String SPRING_PROFILE_DEMO = "demo";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SYSTEM_ACCOUNT = "system";

    public static final String NG_TRANSLATE_LANG_KEY = "OO_LANGUAGE";
    public static final String OPINTONI_LAST_LOGIN = "OPINTONI_LAST_LOGIN";
    public static final String SESSION_COOKIE_NAME = "OPINTONI_JSESSIONID";
    public static final String OPINTONI_HAS_LOGGED_IN = "OPINTONI_HAS_LOGGED_IN";

    public static final String ADMIN_ROLE_REQUIRED = "hasAuthority('ADMIN')";
    public static final String TEACHER_ROLE_REQUIRED = "hasAuthority('TEACHER')";
    public static final String STUDENT_ROLE_REQUIRED = "hasAuthority('STUDENT')";
}
