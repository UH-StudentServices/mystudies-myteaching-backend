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

package fi.helsinki.opintoni.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.primitives.Ints;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class WebTestUtils {

    public static byte[] toJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsBytes(object);
    }

    public static void assertLocalDateTimeJsonArray(ResultActions resultActions, String path, LocalDateTime localDateTime) throws Exception {
        List<Integer> dateElements = createDateElementArrayWithoutLastZeroValueElements(localDateTime);

        IntStream.range(0, dateElements.size()).forEach(index -> assertDateArrayElement(resultActions, path, index, dateElements.get(index)));
    }

    private static List<Integer> createDateElementArrayWithoutLastZeroValueElements(LocalDateTime localDateTime) {
        List<Integer> fixedDateElements = Ints.asList(
            localDateTime.getYear(),
            localDateTime.getMonthValue(),
            localDateTime.getDayOfMonth(),
            localDateTime.getHour(),
            localDateTime.getMinute());

        List<Integer> dateElements = new ArrayList<>();
        dateElements.addAll(fixedDateElements);

        if(localDateTime.getNano() > 0) {
            dateElements.add(localDateTime.getSecond());
            dateElements.add(localDateTime.getNano());
        } else if(localDateTime.getSecond() > 0) {
            dateElements.add(localDateTime.getSecond());
        }

        return dateElements;
    }

    private static void assertDateArrayElement(ResultActions resultActions, String path, int index, int expectedValue) {
        try {
            resultActions.andExpect(jsonPath(String.format("%s[%s]", path, index)).value(expectedValue));
        } catch (Exception e) {
            throw new RuntimeException("Error when asserting array element");
        }
    }

}
