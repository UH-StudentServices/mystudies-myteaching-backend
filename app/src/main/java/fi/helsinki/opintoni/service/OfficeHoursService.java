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

import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.OfficeHoursConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfficeHoursService {
    public final OfficeHoursRepository officeHoursRepository;
    private final UserRepository userRepository;
    private final OfficeHoursConverter officeHoursConverter;

    @Autowired
    public OfficeHoursService(OfficeHoursRepository officeHoursRepository, UserRepository userRepository, OfficeHoursConverter officeHoursConverter) {
        this.officeHoursRepository = officeHoursRepository;
        this.userRepository = userRepository;
        this.officeHoursConverter = officeHoursConverter;
    }

    public OfficeHoursDto update(final Long userId, final OfficeHoursDto officeHoursDto) {
        OfficeHours officeHours = officeHoursRepository.findByUserId(userId);
        if (officeHours == null) {
            officeHours = new OfficeHours();
            officeHours.user = userRepository.findOne(userId);
        }

        officeHours.description = officeHoursDto.description;
        return officeHoursConverter.toDto(officeHoursRepository.save(officeHours));
    }

    public OfficeHoursDto delete(final Long userId) {
        OfficeHours officeHours = officeHoursRepository.findByUserId(userId);
        if (officeHours == null) {
            officeHours = new OfficeHours();
            officeHours.user = userRepository.findOne(userId);
        }

        officeHours.description = null;
        return officeHoursConverter.toDto(officeHoursRepository.save(officeHours));
    }

    public OfficeHoursDto getByUserId(final Long userId) {
        return officeHoursConverter.toDto(officeHoursRepository.findByUserId(userId));
    }
}
