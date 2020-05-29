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

package fi.helsinki.opintoni.integration;

import org.apache.commons.lang3.StringUtils;

public final class IntegrationUtil {

    public static final String SISU_OODI_COURSE_UNIT_REALISATION_ID_PREFIX = "hy-cur-";
    public static final String SISU_OPTIME_COURSE_UNIT_REALISATION_PREFIX = "hy-opt-cur-";
    public static final String SISU_NATIVE_COURSE_UNIT_REALISATION_PREFIX = "otm-";

    public static final String SISU_PRIVATE_PERSON_ID_PREFIX = "hy-hlo-";

    public static String getSisuCourseUnitRealisationId(String id) {
        if (StringUtils.startsWithAny(id.toLowerCase(),
            SISU_OODI_COURSE_UNIT_REALISATION_ID_PREFIX,
            SISU_OPTIME_COURSE_UNIT_REALISATION_PREFIX,
            SISU_NATIVE_COURSE_UNIT_REALISATION_PREFIX)) {
            return id;
        }
        return SISU_OODI_COURSE_UNIT_REALISATION_ID_PREFIX + id;
    }

    public static String getSisuPrivatePersonId(String id) {
        if (StringUtils.startsWithIgnoreCase(id, SISU_PRIVATE_PERSON_ID_PREFIX)) {
            return id;
        }
        return SISU_PRIVATE_PERSON_ID_PREFIX + id;
    }

    private IntegrationUtil() {
    }
}
