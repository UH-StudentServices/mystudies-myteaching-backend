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
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.sampledata.StudyAttainmentSampleData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StudyAttainmentServiceTest extends SpringTest {

    private static final Long MISSING_WHITELIST_PORTFOLIO_ID = 999L;

    @Autowired
    private StudyAttainmentService studyAttainmentService;

    @Test
    public void thatStudyAttainmentsByStudentNumberAreReturned() throws IOException {
        defaultStudentRequestChain().attainments();

        int limitStudyAttainments = 1;

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getStudyAttainments(
            TestConstants.STUDENT_NUMBER, limitStudyAttainments, Locale.ENGLISH);
        assertEquals(limitStudyAttainments, studyAttainments.size());

        StudyAttainmentDto studyAttainmentDto = Iterables.getOnlyElement(studyAttainments);
        assertStudyAttainmentDto(studyAttainmentDto);
    }

    @Test
    public void thatStudyAttainmentsAreOrderedByDate() throws IOException {
        defaultStudentRequestChain().attainments();

        int limitStudyAttainments = 4;

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getStudyAttainments(
            TestConstants.STUDENT_NUMBER, limitStudyAttainments, Locale.ENGLISH);
        assertEquals(limitStudyAttainments, studyAttainments.size());

        List<LocalDateTime> dates = studyAttainments.stream().map(s -> s.attainmentDate).collect(Collectors.toList());

        assertTrue(dates.get(0).isAfter(dates.get(1)));
        assertTrue(dates.get(1).isAfter(dates.get(2)));
        assertTrue(dates.get(2).isAfter(dates.get(3)));

    }

    @Test
    public void thatOnlyWhitelistedAttainmentsAreReturnedForPortfolio() {
        defaultStudentRequestChain().roles().attainments();

        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getWhitelistedAttainmentsByPortfolioId(
            TestConstants.PORTFOLIO_ID,
            Locale.ENGLISH);

        assertEquals(2, studyAttainments.size());
        assertEquals(new Long(1L), studyAttainments.get(0).studyAttainmentId);
        assertEquals(new Long(2), studyAttainments.get(1).studyAttainmentId);
    }

    @Test
    public void shouldReturnEmptyListOnMissingWhitelist() {
        List<StudyAttainmentDto> studyAttainments = studyAttainmentService.getWhitelistedAttainmentsByPortfolioId(
            MISSING_WHITELIST_PORTFOLIO_ID,
            Locale.ENGLISH);

        assertTrue(studyAttainments.isEmpty());
    }

    private void assertStudyAttainmentDto(StudyAttainmentDto studyAttainmentDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        assertEquals(ATTAINMENT_DATE, studyAttainmentDto.attainmentDate.format(formatter));
        assertEquals(CREDITS, studyAttainmentDto.credits);
        assertEquals(GRADE, studyAttainmentDto.grade);
        assertEquals(LEARNING_OPPORTINITY_NAME, studyAttainmentDto.learningOpportunityName);
        assertEquals(
            TEACHERS.stream()
                .map(t1 -> t1.shortName)
                .collect(Collectors.toList()),
            studyAttainmentDto.teachers.stream()
                .map(t2 -> t2.shortName)
                .collect(Collectors.toList())
        );
    }

}
