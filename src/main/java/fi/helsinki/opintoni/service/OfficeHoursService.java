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

import fi.helsinki.opintoni.domain.DegreeProgramme;
import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.domain.PersistentTeachingLanguage;
import fi.helsinki.opintoni.domain.TeachingLanguage;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.dto.PublicOfficeHoursDto;
import fi.helsinki.opintoni.dto.TeachingLanguageDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.exception.http.UnprocessableEntityException;
import fi.helsinki.opintoni.repository.DegreeProgrammeRepository;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.repository.TeachingLanguageRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.OfficeHoursConverter;
import fi.helsinki.opintoni.util.NameSorting;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfficeHoursService {

    // String used to catenate multiple office hours in getAll
    public static final String OFFICE_HOURS_JOIN = " ; ";

    private final OfficeHoursRepository officeHoursRepository;
    private final DegreeProgrammeRepository degreeProgrammeRepository;
    private final TeachingLanguageRepository teachingLanguageRepository;
    private final UserRepository userRepository;
    private final OfficeHoursConverter officeHoursConverter;

    @Autowired
    public OfficeHoursService(OfficeHoursRepository officeHoursRepository,
                              DegreeProgrammeRepository degreeProgrammeRepository,
                              TeachingLanguageRepository teachingLanguageRepository,
                              UserRepository userRepository,
                              OfficeHoursConverter officeHoursConverter) {
        this.officeHoursRepository = officeHoursRepository;
        this.degreeProgrammeRepository = degreeProgrammeRepository;
        this.teachingLanguageRepository = teachingLanguageRepository;
        this.userRepository = userRepository;
        this.officeHoursConverter = officeHoursConverter;
    }

    public List<OfficeHoursDto> update(final Long userId, final List<OfficeHoursDto> officeHoursDtoList) {
        officeHoursRepository.deleteByUserId(userId);
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        validateOfficeHours(officeHoursDtoList);

        return officeHoursDtoList.stream()
            .map(dto -> {
                OfficeHours officeHours = new OfficeHours();
                officeHours.user = user;
                officeHours.description = dto.description;
                officeHours.name = dto.name;
                officeHours.additionalInfo = dto.additionalInfo;
                officeHours.location = dto.location;
                officeHours.degreeProgrammes = degreeProgrammesFromDtos(dto.degreeProgrammes);
                officeHours.teachingLanguages = teachingLanguagesFromDtos(dto.languages);
                officeHours.expirationDate = dto.expirationDate;
                return officeHoursRepository.save(officeHours);
            })
            .map(officeHoursConverter::toDto)
            .collect(Collectors.toList());
    }

    public void delete(final Long userId) {
        officeHoursRepository.deleteByUserId(userId);
    }

    public List<OfficeHoursDto> getByUserId(final Long userId) {
        List<OfficeHours> officeHours = officeHoursRepository.findByUserId(userId);
        return officeHours.stream()
            .map(officeHoursConverter::toDto)
            .collect(Collectors.toList());
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

                officeHoursDto.degreeProgrammes = groupedOfficeHours.get(name).stream()
                    .flatMap(oh -> oh.degreeProgrammes.stream().map(dp -> dp.degreeCode))
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                officeHoursDto.languages = groupedOfficeHours.get(name).stream()
                    .flatMap(oh -> oh.teachingLanguages.stream().map(tl -> tl.language))
                    .distinct()
                    .map(TeachingLanguage::toDto)
                    .collect(Collectors.toList());

                officeHoursDto.officeHours = groupedOfficeHours.get(name).stream()
                    .map(oh -> oh.description)
                    .collect(Collectors.joining(OFFICE_HOURS_JOIN));

                officeHoursDto.name = name;

                return officeHoursDto;
            }).collect(Collectors.toList());
    }

    public List<TeachingLanguageDto> getTeachingLanguages() {
        return Arrays.stream(TeachingLanguage.values())
            .map(TeachingLanguage::toDto)
            .collect(Collectors.toList());
    }

    private List<DegreeProgramme> degreeProgrammesFromDtos(List<DegreeProgrammeDto> degreeProgrammesDtos) {
        return degreeProgrammesDtos.stream()
            .map(dto -> dto.code)
            .distinct()
            .map(code -> {
                DegreeProgramme degreeProgramme = degreeProgrammeRepository
                    .findFirstByDegreeCode(code);
                if (degreeProgramme == null) {
                    degreeProgramme = new DegreeProgramme();
                    degreeProgramme.degreeCode = code;
                    degreeProgramme = degreeProgrammeRepository.save(degreeProgramme);
                }
                return degreeProgramme;
            })
            .collect(Collectors.toList());
    }

    private List<PersistentTeachingLanguage> teachingLanguagesFromDtos(List<TeachingLanguageDto> teachingLanguageDtos) {
        return teachingLanguageDtos.stream()
            .filter(dto -> TeachingLanguage.getCodes().contains(dto.code))
            .map(dto -> dto.code)
            .distinct()
            .map(code -> {
                TeachingLanguage language = TeachingLanguage.fromCode(code);
                PersistentTeachingLanguage teachingLanguage = teachingLanguageRepository.findFirstByLanguage(language).orElse(null);
                if (teachingLanguage == null) {
                    teachingLanguage = new PersistentTeachingLanguage();
                    teachingLanguage.language = language;
                    teachingLanguage = teachingLanguageRepository.save(teachingLanguage);
                }
                return teachingLanguage;
            })
            .collect(Collectors.toList());
    }

    private void validateOfficeHours(List<OfficeHoursDto> officeHoursList) {
        officeHoursList.forEach(officeHours -> {
            validateDegreeProgrammesAndTeachingLanguagesNotBothSet(officeHours);
            validateTeachingLanguages(officeHours);
        });
    }

    private void validateDegreeProgrammesAndTeachingLanguagesNotBothSet(OfficeHoursDto officeHours) {
        if (CollectionUtils.isNotEmpty(officeHours.degreeProgrammes) && CollectionUtils.isNotEmpty(officeHours.languages)) {
            throw new UnprocessableEntityException("degree programmes and teaching languages can't be both set");
        }
    }

    private void validateTeachingLanguages(OfficeHoursDto officeHours) {
        officeHours.languages.forEach(lang -> TeachingLanguage.fromCode(lang.code));
    }
}
