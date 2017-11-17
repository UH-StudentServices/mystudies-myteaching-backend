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

package fi.helsinki.opintoni.service.v2;

import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.dto.v2.PublicOfficeHoursDto;
import fi.helsinki.opintoni.dto.v2.PublicOfficeHoursReceptionDto;
import fi.helsinki.opintoni.repository.DegreeProgrammeRepository;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.util.NameSorting;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OfficeHoursServiceV2  {

    private final OfficeHoursRepository officeHoursRepository;
    private final DegreeProgrammeRepository degreeProgrammeRepository;

    @Autowired
    public OfficeHoursServiceV2(OfficeHoursRepository officeHoursRepository,
        DegreeProgrammeRepository degreeProgrammeRepository) {
        this.officeHoursRepository = officeHoursRepository;
        this.degreeProgrammeRepository = degreeProgrammeRepository;
    }

    public List<PublicOfficeHoursDto> getAll() {
        List<OfficeHours> allOfficeHours = officeHoursRepository.findAll();

        Map<String, List<OfficeHours>> groupedOfficeHours = allOfficeHours.stream()
            .filter(oh -> oh.name != null)
            .collect(Collectors.groupingBy(oh -> oh.name));

        return groupedOfficeHours.keySet().stream()
            .sorted(NameSorting::compareNames)
            .map(name -> {
                PublicOfficeHoursDto officeHoursDto = new PublicOfficeHoursDto();
                officeHoursDto.officeHours = groupedOfficeHours.get(name).stream().map(oh -> {
                    PublicOfficeHoursReceptionDto publicOfficeHoursReceptionDto =
                        new PublicOfficeHoursReceptionDto();
                    publicOfficeHoursReceptionDto.description = oh.description;
                    publicOfficeHoursReceptionDto.additionalInfo = oh.additionalInfo;
                    publicOfficeHoursReceptionDto.location = oh.location;
                    publicOfficeHoursReceptionDto.degreeProgrammes = oh.degreeProgrammes.stream()
                        .map(dp -> dp.degreeCode)
                        .collect(Collectors.toList());
                    return publicOfficeHoursReceptionDto;
                }).collect(Collectors.toList());

                officeHoursDto.name = name;

                return officeHoursDto;
            }).collect(Collectors.toList());
    }

}
