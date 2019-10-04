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

import java.time.LocalDate;
import java.util.List;

public class OfficeHoursDtoBuilder {

    private Long id;
    private String name;
    private String description;
    private String additionalInfo;
    private String location;
    private List<DegreeProgrammeDto> degreeProgrammes;
    private List<TeachingLanguageDto> languages;
    private LocalDate expirationDate;

    public OfficeHoursDtoBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public OfficeHoursDtoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public OfficeHoursDtoBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public OfficeHoursDtoBuilder setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public OfficeHoursDtoBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public OfficeHoursDtoBuilder setDegreeProgrammes(List<DegreeProgrammeDto> degreeProgrammes) {
        this.degreeProgrammes = degreeProgrammes;
        return this;
    }

    public OfficeHoursDtoBuilder setLanguages(List<TeachingLanguageDto> languages) {
        this.languages = languages;
        return this;
    }

    public OfficeHoursDtoBuilder setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public OfficeHoursDto createOfficeHoursDto() {
        return new OfficeHoursDto(
            id,
            name,
            description,
            additionalInfo,
            location,
            degreeProgrammes,
            languages,
            expirationDate);
    }
}
