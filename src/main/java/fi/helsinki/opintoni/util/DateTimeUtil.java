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

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final int SEMESTER_START_MONTH = 8;
    private static final int SEMESTER_START_MONTH_LIMIT = 5;

    public static String getSemesterStartDateString(LocalDate now) {
        int year = now.getYear();
        int month = now.getMonthValue();
        LocalDateTime semesterStartDate = LocalDateTime.of(month < SEMESTER_START_MONTH ? year - 1 : year, SEMESTER_START_MONTH_LIMIT, 1, 0, 0, 0);
        return semesterStartDate.format(DateTimeFormatter.ofPattern(DateFormatter.UTC_TIME_FORMAT_OODI));
    }

}

