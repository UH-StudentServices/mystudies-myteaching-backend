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

import fi.helsinki.opintoni.dto.OptimeCalendarDto;
import fi.helsinki.opintoni.integration.esb.ESBClient;
import fi.helsinki.opintoni.service.converter.OptimeCalendarConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptimeCalendarService {

    private final ESBClient esbClient;
    private final OptimeCalendarConverter calendarConverter;

    @Autowired
    public OptimeCalendarService(ESBClient esbClient, OptimeCalendarConverter calendarConverter) {
        this.esbClient = esbClient;
        this.calendarConverter = calendarConverter;
    }

    public OptimeCalendarDto getOptimeCalendar(String staffId) {
        return esbClient.getStaffInformation(staffId)
            .map(calendarConverter::toDto)
            .orElseGet(OptimeCalendarDto::new);
    }

}
