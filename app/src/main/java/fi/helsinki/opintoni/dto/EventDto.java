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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class EventDto implements Comparable<EventDto> {

    public enum Source {
        OODI,
        COURSE_PAGE
    }

    public enum Type {
        DEFAULT, EXAM
    }

    public final Type type;
    public final Source source;
    public final LocalDateTime startDate;
    public final LocalDateTime endDate;
    public final String locations;
    public final Integer realisationId;
    public final String title;
    public final String courseTitle;
    public final String courseUri;
    public final String courseImageUri;
    public final String courseMaterialUri;
    public final String moodleUri;
    public final BuildingDto building;
    public final boolean hasMaterial;

    public EventDto(
        Type type,
        Source source,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer realisationId,
        String locations,
        String title,
        String courseTitle,
        String courseUri,
        String courseImageUri,
        String courseMaterialUri,
        String moodleUri,
        boolean hasMaterial,
        BuildingDto building) {

        this.type = type;
        this.source = source;
        this.realisationId = realisationId;
        this.endDate = endDate;
        this.locations = locations;
        this.startDate = startDate;
        this.title = title;
        this.courseTitle = courseTitle;
        this.courseUri = courseUri;
        this.courseImageUri = courseImageUri;
        this.courseMaterialUri = courseMaterialUri;
        this.moodleUri = moodleUri;
        this.hasMaterial = hasMaterial;
        this.building = building;
    }

    public EventDto(
        Type type,
        Source source,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer realisationId,
        String locations,
        String title,
        String courseTitle,
        String courseUri,
        String courseImageUri,
        String courseMaterialUri,
        String moodleUri,
        boolean hasMaterial
        ) {

        this(type,
            source,
            startDate,
            endDate,
            realisationId,
            locations,
            title,
            courseTitle,
            courseUri,
            courseImageUri,
            courseMaterialUri,
            moodleUri,
            hasMaterial,
            null);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("realisationId", realisationId)
            .append("endDate", endDate)
            .append("startDate", startDate)
            .append("locations", locations)
            .append("title", title)
            .append("courseImageUri", courseImageUri)
            .append("moodleUri", moodleUri)
            .append("hasMaterial", hasMaterial)
            .toString();
    }

    @Override
    public final int compareTo(EventDto o) {
        return startDate.compareTo(o.startDate);
    }

}
