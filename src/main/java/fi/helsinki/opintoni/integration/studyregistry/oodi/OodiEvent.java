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

package fi.helsinki.opintoni.integration.studyregistry.oodi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.studyregistry.OptimeExtras;
import fi.helsinki.opintoni.util.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.List;

public class OodiEvent {

    @JsonProperty("room_name_short")
    public String roomName;

    @JsonProperty("realisation_type_code")
    public Integer typeCode;

    @JsonProperty("course_id")
    public Integer realisationId;

    @JsonProperty("realisation_name")
    public List<OodiLocalizedValue> realisationName = Lists.newArrayList();

    @JsonProperty("realisation_root_name")
    public List<OodiLocalizedValue> realisationRootName = Lists.newArrayList();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonProperty("building_address1")
    public String buildingStreet;

    @JsonProperty("building_zipcode")
    public String buildingZipCode;

    @JsonProperty("cancelled")
    public boolean isCancelled;

    @JsonProperty("optimeExtras")
    public OptimeExtras optimeExtras;
}
