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

import fi.helsinki.opintoni.security.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenUniversityService {

    private final UserRoleService userRoleService;

    @Autowired
    public OpenUniversityService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @SuppressWarnings("squid:S3655") // User having either employee number and/or student number is guaranteed in appUser instance creation
    public boolean isOpenUniversityUser(AppUser appUser) {
        if (appUser.isTeacher()) {
            return userRoleService.isOpenUniversityTeacher(appUser.getSisuPersonId());
        } else {
            return userRoleService.isOpenUniversityStudent(appUser.getStudentNumber().get());
        }
    }
}
