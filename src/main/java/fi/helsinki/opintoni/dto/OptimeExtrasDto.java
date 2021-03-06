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

package fi.helsinki.opintoni.dto;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OptimeExtrasDto {

    public String otherNotes;

    public String staffNotes;

    public static OptimeExtrasDto parse(String icalEventDescription) {
        String[] descriptionParts = icalEventDescription.split("\\n\\n", -1);
        if (descriptionParts.length < 2) {
            return null;
        }

        String[] extraParts = descriptionParts[1].split("\\n", -1);
        if (extraParts.length > 0) {
            String extraInfo = StreamSupport
                .stream(Arrays.spliterator(extraParts), false)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
            return extraInfo.isBlank() ? null : new OptimeExtrasDto(extraInfo, null);
        }

        return null;
    }

    public OptimeExtrasDto(String otherNotes, String staffNotes) {
        this.otherNotes = otherNotes;
        this.staffNotes = staffNotes;
    }

    public OptimeExtrasDto(String commaSpaceDelimited) {
        String[] parts = commaSpaceDelimited.split(", ", -1);
        staffNotes = parts.length > 0 ? parts[0] : null;
        otherNotes = parts.length > 1 ? parts[1] : null;
    }

    @Override
    public String toString() {
        return Lists.newArrayList(staffNotes, otherNotes)
            .stream()
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
    }
}
