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

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class OodiUserRoleService {

    private final OodiClient oodiClient;

    @Autowired
    public OodiUserRoleService(OodiClient oodiClient) {
        this.oodiClient = oodiClient;
    }

    @Cacheable(CacheConstants.IS_OPEN_UNIVERSITY_TEACHER)
    public boolean isOpenUniversityTeacher(String teacherNumber) {
        return oodiClient.getTeacherCourses(teacherNumber, Locale.ENGLISH, DateTimeUtil.getLastSemesterStartDateString(LocalDate.now())).stream()
            .map(course -> course.basecode)
            .allMatch(this::isOpenUniversityId);
    }

    @Cacheable(CacheConstants.IS_OPEN_UNIVERSITY_STUDENT)
    public boolean isOpenUniversityStudent(String studentNumber) {
        return oodiClient.getEnrollments(studentNumber, Locale.ENGLISH).stream()
            .map(enrollment -> enrollment.learningOpportunityId)
            .allMatch(this::isOpenUniversityId);
    }

    private boolean isOpenUniversityId(String code) {
        return code.startsWith("A");
    }

}
