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
import fi.helsinki.opintoni.util.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OodiStudyAttainment {

    public List<OodiTeacher> teachers = new ArrayList<>();
    public List<OodiLocalizedValue> grade = new ArrayList<>();
    public Integer credits;

    @JsonProperty("studyattainment_id")
    public Long studyAttainmentId;

    @JsonProperty("learningopportunity_name")
    public List<OodiLocalizedValue> learningOpportunityName = new ArrayList<>();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("attainment_date")
    public LocalDateTime attainmentDate;

}
