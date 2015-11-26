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

package fi.helsinki.opintoni.config.http.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.helsinki.opintoni.config.http.CsvResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class CsvHttpMessageConverter<T> extends AbstractHttpMessageConverter<CsvResponse<T>> {

    private static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("UTF-8"));

    public CsvHttpMessageConverter() {
        super(MEDIA_TYPE);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return CsvResponse.class.equals(clazz);
    }


    @Override
    protected CsvResponse<T> readInternal(Class<? extends CsvResponse<T>> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(CsvResponse<T> response, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {

        outputMessage.getHeaders().set("Content-Disposition", "attachment; filename=" + response.filename);

        String csv = getCsv(response);
        StreamUtils.copy(csv, Charset.forName("UTF-8"), outputMessage.getBody());
    }

    private String getCsv(CsvResponse<T> response) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(response.getType());
        return response.entries.stream()
            .map(entry -> toCsvRow(csvMapper, csvSchema, entry))
            .collect(Collectors.joining());
    }

    private String toCsvRow(CsvMapper csvMapper, CsvSchema csvSchema, Object entry) {
        try {
            return csvMapper.writer(csvSchema).writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
