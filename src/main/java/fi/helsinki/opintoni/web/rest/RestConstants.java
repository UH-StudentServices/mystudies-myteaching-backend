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

package fi.helsinki.opintoni.web.rest;

public class RestConstants {

    public static final String PRIVATE_API_V1 = "/api/private/v1";
    public static final String RESTRICTED_API_V1 = "/api/restricted/v1";
    public static final String PUBLIC_API_V1 = "/api/public/v1";

    public static final String PRIVATE_API_V1_PROFILE = PRIVATE_API_V1 + "/profile";
    public static final String RESTRICTED_API_V1_PROFILE = RESTRICTED_API_V1 + "/profile";
    public static final String PUBLIC_API_V1_PROFILE = PUBLIC_API_V1 + "/profile";

    public static final String PRIVATE_FILES_API_V1 = PRIVATE_API_V1_PROFILE + "/files";
    public static final String PUBLIC_FILES_API_V1 = PUBLIC_API_V1_PROFILE + "/files";

    public static final String PUBLIC_API_V2 = "/api/public/v2";
    public static final String ADMIN_API_V1 = "/api/admin/v1";

    public static final String MATCH_NUMBER = "^[0-9]+$";

    private RestConstants() {
    }
}
