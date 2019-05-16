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

package fi.helsinki.opintoni.service.profile;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.service.converter.StudyAttainmentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class StudyAttainmentService {

    private final StudyRegistryService studyRegistryService;
    private final StudyAttainmentConverter studyAttainmentConverter;
    private final StudyAttainmentTransactionalService studyAttainmentTransactionalService;

    @Autowired
    public StudyAttainmentService(StudyRegistryService studyRegistryService,
                                  StudyAttainmentConverter studyAttainmentConverter,
                                  StudyAttainmentTransactionalService studyAttainmentTransactionalService) {
        this.studyRegistryService = studyRegistryService;
        this.studyAttainmentConverter = studyAttainmentConverter;
        this.studyAttainmentTransactionalService = studyAttainmentTransactionalService;
    }

    private int compareStudyAttainments(StudyAttainmentDto s1, StudyAttainmentDto s2) {
        return s1.attainmentDate.compareTo(s2.attainmentDate);
    }

    public List<StudyAttainmentDto> getWhitelistedAttainmentsByProfileId(Long profileId, Locale locale) {
        Profile profile = studyAttainmentTransactionalService.findProfile(profileId);
        return studyAttainmentTransactionalService.findByProfileId(profileId)
            .map(whitelist -> {
                List<Long> whitelistedAttainmentIds = getWhitelistedAttainmentIds(whitelist);
                List<StudyAttainment> studyAttainments =
                    studyRegistryService.getStudyAttainments(profile.user.personId);
                return getWhitelistedAttainments(studyAttainments, whitelistedAttainmentIds, locale,
                    whitelist.showGrades);
            }).orElse(Lists.newArrayList());
    }

    private List<StudyAttainmentDto> getWhitelistedAttainments(List<StudyAttainment> studyAttainments,
                                                               List<Long> whitelistedAttainmentIds,
                                                               Locale locale,
                                                               boolean includeGrades) {
        Comparator<StudyAttainmentDto> studyAttainmentDtoComparator = this::compareStudyAttainments;
        return studyAttainments.stream()
            .filter(a -> whitelistedAttainmentIds.contains(a.studyAttainmentId))
            .map(a -> studyAttainmentConverter.toDto(a, locale, includeGrades))
            .sorted(studyAttainmentDtoComparator.reversed())
            .collect(Collectors.toList());
    }

    private List<Long> getWhitelistedAttainmentIds(StudyAttainmentWhitelist whitelist) {
        return whitelist.whitelistEntries.stream()
            .map(entry -> entry.studyAttainmentId)
            .collect(Collectors.toList());
    }

    public List<StudyAttainmentDto> getStudyAttainments(String personId, String studentNumber, int limit, Locale locale) {
        Comparator<StudyAttainmentDto> studyAttainmentDtoComparator = this::compareStudyAttainments;

        return studyRegistryService.getStudyAttainments(personId, studentNumber).stream()
            .map(a -> studyAttainmentConverter.toDto(a, locale, true))
            .sorted(studyAttainmentDtoComparator.reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

}
