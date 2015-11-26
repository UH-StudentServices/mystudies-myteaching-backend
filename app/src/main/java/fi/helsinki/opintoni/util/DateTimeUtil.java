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

import fi.helsinki.opintoni.integration.DateFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtil {

    private static final int SEMESTER_START_MONTH = 8;

    public static String getLastSemesterStartDateString(LocalDate now) {
        int year = now.getYear();
        int month = now.getMonthValue();
        LocalDateTime d = LocalDateTime.of(month < SEMESTER_START_MONTH ? year - 2 : year - 1, SEMESTER_START_MONTH, 1, 0, 0, 0);
        return d.format(DateFormatter.OODI_DATE_FORMATTER);
    }

}

