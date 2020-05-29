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

package fi.helsinki.opintoni.integration.coursecms;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CourseCmsCourseUnitRealisation implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("field_course_unit_realisation_id")
    public String courseUnitRealisationId;
    @JsonProperty("field_introduction_title")
    public String name;
    @JsonProperty("field_image")
    public CourseCmsFile courseImage;
    @JsonProperty("field_moodle_link")
    public CourseCmsLink moodleLink;
}
