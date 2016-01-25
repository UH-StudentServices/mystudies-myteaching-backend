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

import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.dto.TeacherDto;
import fi.helsinki.opintoni.integration.oodi.OodiStudyAttainment;
import fi.helsinki.opintoni.integration.oodi.OodiTeacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class StudyAttainmentConverter {

    private final LocalizedValueConverter localizedValueConverter;

    @Autowired
    public StudyAttainmentConverter(LocalizedValueConverter localizedValueConverter) {
        this.localizedValueConverter = localizedValueConverter;
    }

    public StudyAttainmentDto toDto(OodiStudyAttainment oodiStudyAttainment, Locale locale) {
        return new StudyAttainmentDto(
            oodiStudyAttainment.studyAttainmentId,
            localizedValueConverter.toLocalizedString(oodiStudyAttainment.learningOpportunityName, locale),
            oodiStudyAttainment.teachers.stream()
                .map(this::convertOodiTeacherToDto)
                .collect(Collectors.toList()),
            oodiStudyAttainment.attainmentDate,
            oodiStudyAttainment.grade,
            oodiStudyAttainment.credits
        );
    }

    private TeacherDto convertOodiTeacherToDto(OodiTeacher oodiTeacher) {
        return new TeacherDto(oodiTeacher.shortName);
    }

}
