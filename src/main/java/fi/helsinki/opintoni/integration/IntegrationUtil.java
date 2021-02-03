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

    public static final String SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX = "hy-CUR-";
    public static final String SISU_COURSE_UNIT_REALISATION_FROM_OPTIME_ID_PREFIX = "hy-opt-cur-";
    public static final String SISU_COURSE_UNIT_REALISATION_PREFIX = "otm-";

    public static String getSisuCourseUnitRealisationId(String id) {
        if (StringUtils.startsWithAny(id,
            SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX,
            SISU_COURSE_UNIT_REALISATION_FROM_OPTIME_ID_PREFIX,
            SISU_COURSE_UNIT_REALISATION_PREFIX)) {
            return id;
        }
        return SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX + id;
    }

    public static String stripPossibleSisuOodiCurPrefix(String curId) {
        if (curId.startsWith(IntegrationUtil.SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX)) {
            return curId.substring(SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX.length());
        }

        return curId;
    }

    private IntegrationUtil() {
    }
}
