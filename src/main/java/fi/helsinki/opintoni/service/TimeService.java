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

package fi.helsinki.opintoni.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
public class TimeService {

    public static final ZoneId HELSINKI_ZONE_ID = ZoneId.of("Europe/Helsinki");

    public DateTime monthsAgo(int months) {
        return DateTime.now().minusMonths(months);
    }

    public LocalDateTime endOfDay(LocalDateTime fromLocalDateTime) {
        return fromLocalDateTime
            .withHour(23)
            .withMinute(59)
            .withSecond(59);
    }

    public String toHelsinkiTimeFromUTC(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone(HELSINKI_ZONE_ID)))
            .toString("dd.MM.yyyy HH:mm");
    }
}
