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

package fi.helsinki.opintoni.integration.oodi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.DateFormatter;

import java.time.LocalDateTime;
import java.util.List;

public class OodiTeacherCourse {

    @JsonProperty("learningopportunity_id")
    public String basecode;

    @JsonFormat(pattern = DateFormatter.UTC_TIME_FORMAT_OODI)
    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonFormat(pattern = DateFormatter.UTC_TIME_FORMAT_OODI)
    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonProperty("realisation_name")
    public List<OodiLocalizedValue> realisationName = Lists.newArrayList();

    @JsonProperty("course_id")
    public String realisationId;

    @JsonProperty("weboodi_uri")
    public String webOodiUri;

    @JsonProperty("realisation_type_code")
    public Integer realisationTypeCode;

}
