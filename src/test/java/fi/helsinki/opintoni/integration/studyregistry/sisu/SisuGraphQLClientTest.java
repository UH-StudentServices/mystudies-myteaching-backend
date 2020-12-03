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

package fi.helsinki.opintoni.integration.studyregistry.sisu;

import static org.junit.Assert.assertEquals;
import java.time.LocalDate;
import org.junit.Test;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodInputTO;

public class SisuGraphQLClientTest {

    @Test
    public void testDatePeriod() {
        LocalDate start = LocalDate.of(2020, 9, 10);
        SisuGraphQLClient sisu = new SisuGraphQLClient(null, null, null);
        DatePeriodInputTO datePeriod = sisu.getDatePeriod(start);
        System.out.println(datePeriod);
        assertEquals("2020-05-01", datePeriod.getStartDate());
        assertEquals("2022-09-10", datePeriod.getEndDate());
    }
}
