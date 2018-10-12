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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.OodiUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fi.helsinki.opintoni.service.converter.FacultyConverter.OPEN_UNIVERSITY_FACULTY_CODE;

@Component
public class UserFacultyResolver {

    private final OodiClient oodiClient;
    private final OodiUserRoleService oodiUserRoleService;
    private final AppConfiguration appConfiguration;

    @Autowired
    public UserFacultyResolver(OodiClient oodiClient,
                               OodiUserRoleService oodiUserRoleService,
                               AppConfiguration appConfiguration) {
        this.oodiClient = oodiClient;
        this.oodiUserRoleService = oodiUserRoleService;
        this.appConfiguration = appConfiguration;
    }

    public String getTeacherFacultyCode(AppUser appUser) {
        return appUser.getTeacherFacultyCode()
            .orElse(getDefaultFacultyCode());
    }

    public String getStudentFacultyCode(String studentNumber) {
        if (oodiUserRoleService.isOpenUniversityStudent(studentNumber)) {
            return OPEN_UNIVERSITY_FACULTY_CODE;
        }
        List<OodiStudyRight> studyRights = oodiClient.getStudentStudyRights(studentNumber);
        return studyRights.stream()
            .filter(studyRight -> studyRight.priority == 1)
            .findFirst()
            .map(studyRight -> studyRight.faculty)
            .orElse(getDefaultFacultyCode());
    }

    private String getDefaultFacultyCode() {
        return appConfiguration.get("userDefaults.defaultFacultyCode");
    }

    public String getFacultyCode(AppUser appUser) {
        return appUser.isTeacher()
            ? getTeacherFacultyCode(appUser)
            : getStudentFacultyCode(appUser.getStudentNumber().get());
    }

    public boolean isUnknownFaculty(String facultyCode) {
        return appConfiguration.get("facultyLinks." + facultyCode) == null;
    }
}
