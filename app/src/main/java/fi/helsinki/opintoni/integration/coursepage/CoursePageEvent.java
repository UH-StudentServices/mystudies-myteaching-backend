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

package fi.helsinki.opintoni.integration.coursepage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.helsinki.opintoni.util.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class CoursePageEvent {

    public String title;
    public String where;
    public boolean exam;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime begin;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime end;

}
