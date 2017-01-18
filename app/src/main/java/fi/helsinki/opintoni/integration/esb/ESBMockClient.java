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

package fi.helsinki.opintoni.integration.esb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public class ESBMockClient implements ESBClient {

    @Value("classpath:sampledata/esb/employeeinfo.json")
    private Resource employeeInfo;

    @Value("classpath:sampledata/esb/staff.json")
    private Resource staff;

    private final ObjectMapper objectMapper;

    public ESBMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber) {
        try {
            return objectMapper.readValue(employeeInfo.getInputStream(), new TypeReference<List<ESBEmployeeInfo>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OptimeStaffInformation getStaffInformation(String staffId) {
        try {
            return objectMapper.readValue(staff.getInputStream(), new TypeReference<OptimeStaffInformation>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
