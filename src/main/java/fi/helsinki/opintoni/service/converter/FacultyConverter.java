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

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.dto.FacultyDto;
import fi.helsinki.opintoni.resolver.UserFacultyResolver;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.OpenUniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FacultyConverter {

    public static final String OPEN_UNIVERSITY_FACULTY_CODE = "A93000";

    private final OpenUniversityService userService;
    private final UserFacultyResolver userFacultyResolver;

    @Autowired
    public FacultyConverter(OpenUniversityService userService,
                            AppConfiguration appConfiguration,
                            UserFacultyResolver
                                userFacultyResolver) {
        this.userService = userService;
        this.userFacultyResolver = userFacultyResolver;
    }

    public FacultyDto getFacultyDto(AppUser appUser) {
        if (userService.isOpenUniversityUser(appUser)) {
            return createOpenUniversityFaculty();
        } else {
            return createFaculty(appUser);
        }
    }

    private FacultyDto createFaculty(AppUser appUser) {
        String facultyCode = userFacultyResolver.getFacultyCode(appUser);
        if (userFacultyResolver.isUnknownFaculty(facultyCode)) {
            return null;
        }
        return new FacultyDto(facultyCode);
    }

    private FacultyDto createOpenUniversityFaculty() {
        String openUniversityCode = OPEN_UNIVERSITY_FACULTY_CODE;
        return new FacultyDto(openUniversityCode);
    }
}
