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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import fi.helsinki.opintoni.integration.DateFormatter;

public class DateTimeUtil {

    private static final int SEMESTER_START_MONTH = 8;
    private static final int SEMESTER_START_MONTH_LIMIT = 5;

    public static String getSemesterStartDateOodiString(LocalDate now) {
        return getSemesterStartDate(now).atStartOfDay().format(DateTimeFormatter.ofPattern(DateFormatter.UTC_TIME_FORMAT_OODI));
    }

    public static String getSemesterStartDateSisuString(LocalDate now) {
        return getSemesterStartDate(now).format(DateTimeFormatter.ofPattern(DateFormatter.DATE_FORMAT_SISU));
    }

    public static String getSisuDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DateFormatter.DATE_FORMAT_SISU));
    }

    public static LocalDate getSemesterStartDate(LocalDate now) {
        int year = now.getYear();
        int month = now.getMonthValue();
        return LocalDate.of(month < SEMESTER_START_MONTH ? year - 1 : year, SEMESTER_START_MONTH_LIMIT, 1);
    }

}

