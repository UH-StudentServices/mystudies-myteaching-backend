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

public class OodiEnrollment {

    public List<OodiLocalizedValue> name = Lists.newArrayList();

    public Integer credits;

    @JsonProperty("learningopportunity_id")
    public String learningOpportunityId;

    @JsonProperty("type_code")
    public Integer typeCode;

    @JsonFormat(pattern = DateFormatter.UTC_TIME_FORMAT_OODI)
    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonFormat(pattern = DateFormatter.UTC_TIME_FORMAT_OODI)
    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonProperty("weboodi_uri")
    public String webOodiUri;

    @JsonProperty("course_id")
    public String realisationId;

    @JsonProperty("parent_id")
    public String parentId;

    @JsonProperty("root_id")
    public String rootId;

}