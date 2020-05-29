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
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class CourseCmsResponseWrapper {

    @JsonProperty("data")
    List<CourseCmsCourseUnitRealisation> data;
    @JsonProperty("errors")
    List<CmsResponseError> errors;

    public static class CmsResponseError {
        public String title;
        public String status;
        public String detail;

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("title", title)
                .append("status", status)
                .append("detail", detail)
                .toString();
        }
    }
}
