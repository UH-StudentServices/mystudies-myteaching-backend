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

import fi.helsinki.opintoni.dto.profile.CourseMaterialDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventDto implements Comparable<EventDto> {

    public enum Source {
        STUDY_REGISTRY,
        COURSE_PAGE,
        OPTIME
    }

    public enum Type {
        DEFAULT,
        EXAM
    }

    public final Type type;
    public final Source source;
    public final LocalDateTime startDate;
    public final LocalDateTime endDate;
    public final Integer realisationId;
    public final String title;
    public final String courseTitle;
    public final String courseUri;
    public final String courseImageUri;
    public final CourseMaterialDto courseMaterial;
    public final String moodleUri;
    public final List<LocationDto> locations;
    public final boolean hasMaterial;
    public final OptimeExtrasDto optimeExtras;
    public final String uid;
    public final boolean isHidden;

    EventDto(Type type,
             Source source,
             LocalDateTime startDate,
             LocalDateTime endDate,
             Integer realisationId,
             String title,
             String courseTitle,
             String courseUri,
             String courseImageUri,
             CourseMaterialDto courseMaterialDto,
             String moodleUri,
             boolean hasMaterial,
             List<LocationDto> locations,
             OptimeExtrasDto optimeExtras,
             String uid,
             boolean isHidden) {
        this.type = type;
        this.source = source;
        this.realisationId = realisationId;
        this.endDate = endDate;
        this.startDate = startDate;
        this.title = title;
        this.courseTitle = courseTitle;
        this.courseUri = courseUri;
        this.courseImageUri = courseImageUri;
        this.courseMaterial = courseMaterialDto;
        this.moodleUri = moodleUri;
        this.hasMaterial = hasMaterial;
        this.locations = locations;
        this.optimeExtras = optimeExtras;
        this.uid = uid;
        this.isHidden = isHidden;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("realisationId", realisationId)
            .append("endDate", endDate)
            .append("startDate", startDate)
            .append("locationString", this.getLocationsAsString())
            .append("title", title)
            .append("courseImageUri", courseImageUri)
            .append("moodleUri", moodleUri)
            .append("hasMaterial", hasMaterial)
            .append("isHidden", isHidden)
            .toString();
    }

    @Override
    public final int compareTo(EventDto o) {
        return startDate.compareTo(o.startDate);
    }

    public static String getRealisationIdAndTimes(EventDto dto) {
        String result = "";
        if (dto.realisationId != null) {
            result += dto.realisationId;
        }
        if (dto.startDate != null) {
            result += dto.startDate;
        }
        if (dto.endDate != null) {
            result += dto.endDate;
        }
        return result;
    }

    public String getLocationsAsString() {
        return locations.stream()
            .map(LocationDto::getLocationString)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
    }

    public String getOptimeExtrasAsString() {
        return optimeExtras == null ?
            "" :
            optimeExtras.toString();
    }

    public String getFullEventTitle() {
        return Source.COURSE_PAGE.equals(source) ? String.format("%s, %s", title, courseTitle) : title;
    }

}
