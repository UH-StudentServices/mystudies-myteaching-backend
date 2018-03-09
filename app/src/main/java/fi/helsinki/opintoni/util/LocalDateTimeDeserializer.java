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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

import static fi.helsinki.opintoni.service.TimeService.HELSINKI_ZONE_ID;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private  static final Logger log = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);

    public LocalDateTimeDeserializer() {
        this(null);
    }

    public LocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String dateString = jsonParser.getText();
        LocalDateTime localDateTime = null;

        if (dateString != null && !dateString.isEmpty()) {
            try {
                Instant instant = Instant.parse(dateString);
                localDateTime = instant.atZone(HELSINKI_ZONE_ID).toLocalDateTime();
            } catch (Exception e) {
                log.error("Error when parsing value to date: " + dateString, e);
            }
        }

        return localDateTime;
    }
}
