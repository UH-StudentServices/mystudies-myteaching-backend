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

    private final static Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);

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

        if(dateString != null && !dateString.isEmpty()) {
            try {
                Instant instant = Instant.parse(dateString);
                localDateTime = instant.atZone(HELSINKI_ZONE_ID).toLocalDateTime();
            } catch (Exception e) {
                LOGGER.error("Error when parsing value to date: " + dateString, e);
            }
        }

        return localDateTime;
    }
}
