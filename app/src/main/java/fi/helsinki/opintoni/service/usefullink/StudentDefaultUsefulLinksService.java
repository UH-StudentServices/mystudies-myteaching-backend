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
public class StudentDefaultUsefulLinksService extends DefaultUsefulLinksService {

    private final UsefulLinkTransactionalService usefulLinkTransactionalService;
    private final UserFacultyResolver userFacultyResolver;
    private final OodiUserRoleService oodiUserRoleService;
    private final List<Map<String, String>> defaultUsefulLinks;
    private final List<Map<String, String>> openUniversityDefaultUsefulLinks;
    private final List<Map<String, String>> facultyLinkOptions;

    @Autowired
    public StudentDefaultUsefulLinksService(UsefulLinkTransactionalService usefulLinkTransactionalService,
                                            UsefulLinksProperties usefulLinksProperties,
                                            FacultyUsefulLinksProperties facultyLinksProperties,
                                            UserFacultyResolver userFacultyResolver,
                                            OodiUserRoleService oodiUserRoleService) {
        this.usefulLinkTransactionalService = usefulLinkTransactionalService;
        this.userFacultyResolver = userFacultyResolver;
        this.oodiUserRoleService = oodiUserRoleService;
        this.defaultUsefulLinks = usefulLinksProperties.getStudentDefaultUsefulLinks();
        this.openUniversityDefaultUsefulLinks = usefulLinksProperties.getStudentOpenUniversityDefaultUsefulLinks();
        this.facultyLinkOptions = facultyLinksProperties.getStudentFacultyLinks();
    }

    public void createDefaultLinks(User user, AppUser appUser) {
        List<UsefulLink> usefulLinks = oodiUserRoleService.isOpenUniversityStudent(appUser.getStudentNumber().get())
            ? createLocalizedUsefulLinks(openUniversityDefaultUsefulLinks, user)
            : createUsefulLinksByStudentNumber(user, appUser.getStudentNumber().get());

        usefulLinkTransactionalService.save(usefulLinks);
    }

    private List<UsefulLink> createUsefulLinksByStudentNumber(User user, String studentNumber) {
        List<UsefulLink> usefulLinks = createUsefulLinks(defaultUsefulLinks, user);

        String facultyCode = userFacultyResolver.getStudentFacultyCode(studentNumber);
        usefulLinks.addAll(getFacultyUsefulLinks(facultyCode, facultyLinkOptions, user));

        return usefulLinks;
    }

}
