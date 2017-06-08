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

package fi.helsinki.opintoni.service;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.DegreeProgramme;
import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.dto.PublicOfficeHoursDto;
import fi.helsinki.opintoni.repository.DegreeProgrammeRepository;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.OfficeHoursConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfficeHoursService {
    private final OfficeHoursRepository officeHoursRepository;
    private final DegreeProgrammeRepository degreeProgrammeRepository;
    private final UserRepository userRepository;
    private final OfficeHoursConverter officeHoursConverter;


    @Autowired
    public OfficeHoursService(OfficeHoursRepository officeHoursRepository,
                              DegreeProgrammeRepository degreeProgrammeRepository,
                              UserRepository userRepository,
                              OfficeHoursConverter officeHoursConverter) {
        this.officeHoursRepository = officeHoursRepository;
        this.degreeProgrammeRepository = degreeProgrammeRepository;
        this.userRepository = userRepository;
        this.officeHoursConverter = officeHoursConverter;
    }

    private int comparePublicOfficeHoursDtos(PublicOfficeHoursDto p1, PublicOfficeHoursDto p2) {
        return p1.name.compareTo(p2.name);
    }

    public OfficeHoursDto update(final Long userId, final OfficeHoursDto officeHoursDto) {
        OfficeHours officeHours = officeHoursRepository.findByUserId(userId);
        if (officeHours == null) {
            officeHours = new OfficeHours();
            officeHours.user = userRepository.findOne(userId);
        }

        officeHours.description = officeHoursDto.description;
        officeHours.name = officeHoursDto.name;
        officeHours = officeHoursRepository.save(officeHours);

        List<DegreeProgramme> degreeProgrammes = officeHoursDto.degreeProgrammes.stream()
            .distinct()
            .map(degreeProgrammeDto -> {
                DegreeProgramme degreeProgramme = new DegreeProgramme();
                degreeProgramme.user = userRepository.findOne(userId);
                degreeProgramme.degreeCode = degreeProgrammeDto.code;
                return degreeProgramme;
            })
            .collect(Collectors.toList());

        degreeProgrammeRepository.deleteByUserId(userId);
        degreeProgrammes = degreeProgrammeRepository.save(degreeProgrammes);

        return officeHoursConverter.toDto(officeHours, degreeProgrammes);
    }

    public OfficeHoursDto delete(final Long userId) {
        OfficeHours officeHours = officeHoursRepository.findByUserId(userId);
        if (officeHours == null) {
            officeHours = new OfficeHours();
            officeHours.user = userRepository.findOne(userId);
        }

        degreeProgrammeRepository.deleteByUserId(userId);

        officeHours.description = null;
        return officeHoursConverter.toDto(officeHoursRepository.save(officeHours), Lists.newArrayList());
    }

    public OfficeHoursDto getByUserId(final Long userId) {
        OfficeHours officeHours = officeHoursRepository.findByUserId(userId);
        List<DegreeProgramme> degreeProgrammes = degreeProgrammeRepository.findByUserId(userId);

        return officeHoursConverter.toDto(officeHours, degreeProgrammes);
    }

    public List<PublicOfficeHoursDto> getAll() {
        List<OfficeHours> allOfficeHours = officeHoursRepository.findAll();

        return allOfficeHours.stream()
            .filter(officeHours -> officeHours.description != null)
            .map(officeHours -> {
                List<DegreeProgramme> degreeProgrammes = degreeProgrammeRepository.findByUserId(officeHours.getOwnerId());
                List<String> degreeProgrammeCodes = degreeProgrammes.stream()
                    .map(degreeProgramme -> {
                        return degreeProgramme.degreeCode;
                    })
                    .collect(Collectors.toList());

                PublicOfficeHoursDto officeHoursDto = new PublicOfficeHoursDto();
                officeHoursDto.degreeProgrammes = degreeProgrammeCodes;
                officeHoursDto.officeHours = officeHours.description;
                officeHoursDto.name = officeHours.name;

                return officeHoursDto;
            })
            .sorted(this::comparePublicOfficeHoursDtos)
            .collect(Collectors.toList());
    }

}
