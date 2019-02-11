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

import fi.helsinki.opintoni.service.converter.EventConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class OptimeMockClient implements OptimeClient {
    @Autowired
    EventConverter eventConverter;

    @Value("classpath:sampledata/optime/icalendar.ics")
    private Resource icalendar;

    @Override
    public InputStream getICalendarContent(String feedUrl) {
        try {
            return icalendar.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
