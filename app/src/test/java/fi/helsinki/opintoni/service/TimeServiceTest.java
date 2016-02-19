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

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeServiceTest {

    private final TimeService timeService = new TimeService();

    @Test
    public void thatEndOfDayIsReturned() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime localDateTime = LocalDateTime.parse("2015-05-01T11:00:00Z", dateTimeFormatter);

        LocalDateTime endOfDay = timeService.endOfDayHelsinki(localDateTime);

        assertThat(endOfDay.format(dateTimeFormatter)).isEqualTo("2015-05-01T20:59:59Z");
    }
}
