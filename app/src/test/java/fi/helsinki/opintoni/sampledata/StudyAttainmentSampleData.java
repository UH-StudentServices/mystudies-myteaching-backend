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

package fi.helsinki.opintoni.sampledata;

import fi.helsinki.opintoni.dto.TeacherDto;

import java.util.Collections;
import java.util.List;

public class StudyAttainmentSampleData {
    public static final String ATTAINMENT_DATE = "01.01.2016";
    public static final Integer ATTAINMENT_DATE_YEAR = 2016;
    public static final Integer ATTAINMENT_DATE_MONTH = 1;
    public static final Integer ATTAINMENT_DATE_DAY = 1;
    public static final Integer ATTAINMENT_DATE_HOUR = 22;
    public static final Integer ATTAINMENT_DATE_MINUTE = 0;
    public static final Integer CREDITS = 6;
    public static final String GRADE = "5";
    public static final String LEARNING_OPPORTINITY_NAME = "Formulation III";
    public static final String TEACHER_SHORT_NAME = "Opettaja Olli";
    public static final List<TeacherDto> TEACHERS = Collections.singletonList(new TeacherDto(TEACHER_SHORT_NAME));
}
