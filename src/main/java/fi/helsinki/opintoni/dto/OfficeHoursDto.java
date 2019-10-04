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

import fi.helsinki.opintoni.validation.MaxYearFromNow;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Valid
public class OfficeHoursDto {

    public Long id;

    @NotNull
    public String name;

    @NotNull
    public String description;

    public String additionalInfo;

    public String location;

    public List<DegreeProgrammeDto> degreeProgrammes = new ArrayList<>();

    public List<TeachingLanguageDto> languages = new ArrayList<>();

    @NotNull
    @MaxYearFromNow
    public LocalDate expirationDate;

    private OfficeHoursDto() {
        // keeps jackson happy and sonar from complaining about not initialized @NotNull annotated variables
        this.name = "";
        this.description = "";
        this.expirationDate = LocalDate.now();
    }

    OfficeHoursDto(Long id,
                   String name,
                   String description,
                   String additionalInfo,
                   String location,
                   List<DegreeProgrammeDto> degreeProgrammes,
                   List<TeachingLanguageDto> languages,
                   LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.location = location;
        this.degreeProgrammes = degreeProgrammes;
        this.languages = languages;
        this.expirationDate = expirationDate;
    }

    // JSR-303 validation requires getter and setter
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    // JSR-303 validation requires getter and setter
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
