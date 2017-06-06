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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.DegreeProgramme;
import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OfficeHoursConverter {

    public OfficeHoursDto toDto(OfficeHours officeHours, List<DegreeProgramme> degreeProgrammes) {
        OfficeHoursDto officeHoursDto = new OfficeHoursDto();
        if (officeHours != null) {
            officeHoursDto.id = officeHours.id;
            officeHoursDto.description = officeHours.description;

            officeHoursDto.degreeProgrammes = degreeProgrammes.stream()
                .map(degreeProgramme -> {
                    DegreeProgrammeDto degreeProgrammeDto = new DegreeProgrammeDto();
                    degreeProgrammeDto.code = degreeProgramme.degreeCode;
                    return degreeProgrammeDto;
                })
                .collect(Collectors.toList());
        }

        return officeHoursDto;
    }
}
