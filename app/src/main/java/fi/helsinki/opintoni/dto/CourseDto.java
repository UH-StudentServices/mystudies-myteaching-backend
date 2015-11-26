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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDto {
    public final String code;
    public final Integer typeCode;
    public final String name;
    public final String imageUri;
    public final String coursePageUri;
    public final String courseMaterialUri;
    public final String moodleUri;
    public final String webOodiUri;
    public final LocalDateTime startDate;
    public final LocalDateTime endDate;
    public final String realisationId;
    public final Integer credits;
    public final List<String> teachers;
    public final boolean hasMaterial;

    public CourseDto(String code,
                     Integer typeCode,
                     String name,
                     String imageUri,
                     String coursePageUri,
                     String courseMaterialUri,
                     String moodleUri,
                     String webOodiUri,
                     LocalDateTime startDate,
                     LocalDateTime endDate,
                     String realisationId,
                     Integer credits,
                     List<String> teachers,
                     boolean hasMaterial) {
        this.typeCode = typeCode;
        this.code = code;
        this.name = name;
        this.imageUri = imageUri;
        this.coursePageUri = coursePageUri;
        this.courseMaterialUri = courseMaterialUri;
        this.moodleUri = moodleUri;
        this.webOodiUri = webOodiUri;
        this.startDate = startDate;
        this.endDate = endDate;
        this.realisationId = realisationId;
        this.credits = credits;
        this.teachers = teachers;
        this.hasMaterial = hasMaterial;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("code", code)
            .append("typeCode", typeCode)
            .append("name", name)
            .append("imageUri", imageUri)
            .append("coursePageUri", coursePageUri)
            .append("courseMaterialUri", courseMaterialUri)
            .append("moodleUri", moodleUri)
            .append("webOodiUri", webOodiUri)
            .append("startDate", startDate)
            .append("endDate", endDate)
            .append("realisationId", realisationId)
            .append("teachers", teachers.stream().collect(Collectors.joining(", ")))
            .append("hasMaterial", hasMaterial)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CourseDto that = (CourseDto) o;

        return StringUtils.equals(realisationId, that.realisationId);
    }

    @Override
    public int hashCode() {
        return realisationId != null ? realisationId.hashCode() : 0;
    }
}
