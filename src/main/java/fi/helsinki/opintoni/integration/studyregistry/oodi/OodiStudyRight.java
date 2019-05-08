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
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class OodiStudyRight {

    public int priority;

    @JsonProperty("faculty_code")
    public String faculty;

    public List<Element> elements = new ArrayList<>();

    public List<String> getElementCodes() {
        return this.elements.stream()
            .map(element -> element.code)
            .collect(toList());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("priority", priority)
            .add("faculty", faculty)
            .add("elements", elements)
            .toString();
    }

    public static class Element {

        @JsonProperty("element_id")
        public Integer id;
        public String code;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("code", code)
                .toString();
        }
    }
}
