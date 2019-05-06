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

package fi.helsinki.opintoni.service.profile;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.esb.ESBClient;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.converter.LocalizedValueConverter;
import fi.helsinki.opintoni.service.converter.profile.ContactInformationConverter;
import fi.helsinki.opintoni.service.profile.ContactInformationService;
import fi.helsinki.opintoni.service.profile.EmployeeContactInformationService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EmployeeContactInformationServiceTest extends SpringTest {

    private static final String EMPLOYEE_NUMBER_PREFIX = "9";
    private static final String EMPLOYEE_NUMBER = "0123456";
    private static final String PREFIXED_EMPLOYEE_NUMBER = EMPLOYEE_NUMBER_PREFIX + EMPLOYEE_NUMBER;

    private EmployeeContactInformationService employeeContactInformationService;
    private ESBClient mockEsbClient;

    @Before
    public void setup() {
        mockEsbClient = mock(ESBClient.class);
        employeeContactInformationService = new EmployeeContactInformationService(
            mockEsbClient,
            mock(ContactInformationConverter.class),
            mock(LocalizedValueConverter.class),
            mock(ContactInformationService.class)
            );
    }

    @Test
    public void thatTeacherIdPrefixIsAddedWhenMissing() throws Exception {
        employeeContactInformationService.fetchEmployeeContactInformation(1L, EMPLOYEE_NUMBER, Language.FI.toLocale());

        verify(mockEsbClient, times(1)).getEmployeeInfo(PREFIXED_EMPLOYEE_NUMBER);
    }

    @Test
    public void thatTeacherIdPrefixIsNotAddedWhenPresent() throws Exception {
        employeeContactInformationService.fetchEmployeeContactInformation(1L, PREFIXED_EMPLOYEE_NUMBER, Language.FI.toLocale());

        verify(mockEsbClient, times(1)).getEmployeeInfo(PREFIXED_EMPLOYEE_NUMBER);
    }
}
