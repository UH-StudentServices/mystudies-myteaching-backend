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

package fi.helsinki.opintoni.integration.optime;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.helsinki.opintoni.web.TestConstants.EMPLOYEE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

public class optimeRestClientTest extends SpringTest {

    @Autowired
    private OptimeClient optimeRestClient;

    @Test
    public void thatCalendarUrlIsReturned() {
        optimeServer.expectTeacherCalendarRequest(EMPLOYEE_NUMBER);
        OptimeStaffInformation optimeStaffInformation = optimeRestClient.getStaffInformation(EMPLOYEE_NUMBER);
        assertThat(optimeStaffInformation.url).isEqualTo("https://optime.example.com/IcalService/staff/99999");
    }

    @Test
    public void thatEmptyCalendarUrlIsHandled() {
        optimeServer.expectFailedTeacherCalendarRequest(EMPLOYEE_NUMBER);
        OptimeStaffInformation optimeStaffInformation = optimeRestClient.getStaffInformation(EMPLOYEE_NUMBER);
        assertThat(optimeStaffInformation.url).isNull();
    }

}
