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

package fi.helsinki.opintoni.service.usefullink;

import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.resolver.UserFacultyResolver;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.OodiUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeacherDefaultUsefulLinksService extends DefaultUsefulLinksService {

    private final List<Map<String, String>> defaultUsefulLinks;
    private final List<Map<String, String>> openUniversityDefaultUsefulLinks;
    private final List<Map<String, String>> facultyLinkOptions;
    private final UsefulLinkTransactionalService usefulLinkTransactionalService;
    private final OodiUserRoleService oodiUserRoleService;
    private final UserFacultyResolver userFacultyResolver;

    @Autowired
    public TeacherDefaultUsefulLinksService(UsefulLinksProperties usefulLinksProperties,
                                            FacultyUsefulLinksProperties facultyLinksProperties,
                                            UsefulLinkTransactionalService usefulLinkTransactionalService,
                                            OodiUserRoleService oodiUserRoleService,
                                            UserFacultyResolver userFacultyResolver) {
        this.usefulLinkTransactionalService = usefulLinkTransactionalService;
        this.oodiUserRoleService = oodiUserRoleService;
        this.userFacultyResolver = userFacultyResolver;
        this.openUniversityDefaultUsefulLinks = usefulLinksProperties.getTeacherOpenUniversityDefaultUsefulLinks();
        this.defaultUsefulLinks = usefulLinksProperties.getTeacherDefaultUsefulLinks();
        this.facultyLinkOptions = facultyLinksProperties.getTeacherFacultyLinks();
    }

    public void createDefaultLinks(User user, AppUser appUser) {
        List<UsefulLink> usefulLinks = oodiUserRoleService.isOpenUniversityTeacher(appUser.getTeacherNumber().get())
            ? createUsefulLinks(openUniversityDefaultUsefulLinks, user)
            : createUsefulLinksForTeacher(user, appUser);

        usefulLinkTransactionalService.save(usefulLinks);
    }

    private List<UsefulLink> createUsefulLinksForTeacher(User user, AppUser appUser) {
        List<UsefulLink> usefulLinks = createUsefulLinks(defaultUsefulLinks, user);

        String teacherFacultyCode = userFacultyResolver.getTeacherFacultyCode(appUser);
        usefulLinks.addAll(getFacultyUsefulLinks(teacherFacultyCode, facultyLinkOptions, user));

        return usefulLinks;
    }

}
