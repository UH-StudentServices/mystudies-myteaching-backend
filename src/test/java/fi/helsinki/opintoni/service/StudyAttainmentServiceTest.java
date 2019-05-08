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

import com.google.common.collect.Iterables;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.service.profile.StudyAttainmentService;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.ATTAINMENT_DATE;
import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.CREDITS;
import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.GRADE;
import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.LEARNING_OPPORTINITY_NAME;
import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.TEACHERS;
import static fi.helsinki.opintoni.web.TestConstants.DEFAULT_USER_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;

public class StudyAttainmentServiceTest extends SpringTest {
    private static final Long MISSING_WHITELIST_PROFILE_ID = 999L;

    @Autowired
    private StudyAttainmentService studyAttainmentService;

    @Test
    public void thatStudyAttainmentsByStudentNumberAreReturned() throws IOException {
        defaultStudentRequestChain().attainments();

        int limitStudyAttainments = 1;

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getStudyAttainments(
            TestConstants.STUDENT_NUMBER, limitStudyAttainments, DEFAULT_USER_LOCALE);
        assertThat(studyAttainments.size()).isEqualTo(limitStudyAttainments);

        StudyAttainmentDto studyAttainmentDto = Iterables.getOnlyElement(studyAttainments);
        assertStudyAttainmentDto(studyAttainmentDto);
    }

    @Test
    public void thatStudyAttainmentsAreOrderedByDate() throws IOException {
        defaultStudentRequestChain().attainments();

        int limitStudyAttainments = 4;

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getStudyAttainments(
            TestConstants.STUDENT_NUMBER, limitStudyAttainments, DEFAULT_USER_LOCALE);
        assertThat(studyAttainments.size()).isEqualTo(limitStudyAttainments);

        List<LocalDateTime> dates = studyAttainments.stream().map(s -> s.attainmentDate).collect(Collectors.toList());

        assertThat(dates.get(0).isAfter(dates.get(1))).isTrue();
        assertThat(dates.get(1).isAfter(dates.get(2))).isTrue();
        assertThat(dates.get(2).isAfter(dates.get(3))).isTrue();

    }

    @Test
    public void thatOnlyWhitelistedAttainmentsAreReturnedForProfile() {
        defaultStudentRequestChain().roles().attainments();

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getWhitelistedAttainmentsByProfileId(
            TestConstants.PROFILE_ID,
            Locale.ENGLISH);

        assertThat(studyAttainments).hasSize(2);
        assertThat(studyAttainments.get(0).studyAttainmentId).isEqualTo(1L);
        assertThat(studyAttainments.get(1).studyAttainmentId).isEqualTo(2L);
    }

    @Test
    public void shouldReturnEmptyListOnMissingWhitelist() {
        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getWhitelistedAttainmentsByProfileId(
            MISSING_WHITELIST_PROFILE_ID,
            Locale.ENGLISH);

        assertThat(studyAttainments.isEmpty()).isTrue();
    }

    private void assertStudyAttainmentDto(StudyAttainmentDto studyAttainmentDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        assertThat(studyAttainmentDto.attainmentDate.format(formatter)).isEqualTo(ATTAINMENT_DATE);
        assertThat(studyAttainmentDto.credits).isEqualTo(CREDITS);
        assertThat(studyAttainmentDto.grade).isEqualTo(GRADE);
        assertThat(studyAttainmentDto.learningOpportunityName).isEqualTo(LEARNING_OPPORTINITY_NAME);
        assertThat(
            TEACHERS.stream()
                .map(t1 -> t1.name)
                .collect(Collectors.toList()))
            .isEqualTo(
                studyAttainmentDto.teachers.stream()
                    .map(t2 -> t2.name)
                    .collect(Collectors.toList()));
    }
}
