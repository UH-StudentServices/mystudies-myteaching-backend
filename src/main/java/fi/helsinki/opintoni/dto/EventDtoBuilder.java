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
import java.time.LocalDateTime;
import java.util.List;

public class EventDtoBuilder {

    private EventDto.Type type;
    private EventDto.Source source;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer realisationId;
    private String title;
    private String courseTitle;
    private String courseUri;
    private String courseImageUri;
    private CourseMaterialDto courseMaterialDto;
    private String moodleUri;
    private boolean hasMaterial;
    private List<LocationDto> locations;
    private OptimeExtrasDto optimeExtras;
    private String uid;
    private boolean isHidden;

    public EventDtoBuilder() {
    }

    public EventDtoBuilder setType(EventDto.Type type) {
        this.type = type;
        return this;
    }

    public EventDtoBuilder setSource(EventDto.Source source) {
        this.source = source;
        return this;
    }

    public EventDtoBuilder setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public EventDtoBuilder setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public EventDtoBuilder setRealisationId(Integer realisationId) {
        this.realisationId = realisationId;
        return this;
    }

    public EventDtoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventDtoBuilder setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
        return this;
    }

    public EventDtoBuilder setCourseUri(String courseUri) {
        this.courseUri = courseUri;
        return this;
    }

    public EventDtoBuilder setCourseImageUri(String courseImageUri) {
        this.courseImageUri = courseImageUri;
        return this;
    }

    public EventDtoBuilder setCourseMaterialDto(CourseMaterialDto courseMaterialDto) {
        this.courseMaterialDto = courseMaterialDto;
        return this;
    }

    public EventDtoBuilder setMoodleUri(String moodleUri) {
        this.moodleUri = moodleUri;
        return this;
    }

    public EventDtoBuilder setHasMaterial(boolean hasMaterial) {
        this.hasMaterial = hasMaterial;
        return this;
    }

    public EventDtoBuilder setLocations(List<LocationDto> locations) {
        this.locations = locations;
        return this;
    }

    public EventDtoBuilder setOptimeExtras(OptimeExtrasDto optimeExtras) {
        this.optimeExtras = optimeExtras;
        return this;
    }

    public EventDtoBuilder setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public EventDtoBuilder setHidden(boolean isHidden) {
        this.isHidden = isHidden;
        return this;
    }

    public EventDto createEventDto() {
        return new EventDto(
            type,
            source,
            startDate,
            endDate,
            realisationId,
            title,
            courseTitle,
            courseUri,
            courseImageUri,
            courseMaterialDto,
            moodleUri,
            hasMaterial,
            locations,
            optimeExtras,
            uid,
            isHidden);
    }
}
