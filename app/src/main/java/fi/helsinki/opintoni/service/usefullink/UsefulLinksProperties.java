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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "user-defaults.default-useful-links")
public class UsefulLinksProperties {

    private List<Map<String, String>> studentDefaultUsefulLinks = new ArrayList<>();
    private List<Map<String, String>> teacherDefaultUsefulLinks = new ArrayList<>();
    private List<Map<String, String>> studentOpenUniversityDefaultUsefulLinks = new ArrayList<>();
    private List<Map<String, String>> teacherOpenUniversityDefaultUsefulLinks = new ArrayList<>();

    public List<Map<String, String>> getStudentDefaultUsefulLinks() {
        return studentDefaultUsefulLinks;
    }

    public void setStudentDefaultUsefulLinks(List<Map<String, String>> studentDefaultUsefulLinks) {
        this.studentDefaultUsefulLinks = studentDefaultUsefulLinks;
    }

    public List<Map<String, String>> getTeacherDefaultUsefulLinks() {
        return teacherDefaultUsefulLinks;
    }

    public void setTeacherDefaultUsefulLinks(List<Map<String, String>> teacherDefaultUsefulLinks) {
        this.teacherDefaultUsefulLinks = teacherDefaultUsefulLinks;
    }

    public void setStudentOpenUniversityDefaultUsefulLinks(List<Map<String, String>>
                                                               studentOpenUniversityDefaultUsefulLinks) {
        this.studentOpenUniversityDefaultUsefulLinks = studentOpenUniversityDefaultUsefulLinks;
    }

    public List<Map<String, String>> getStudentOpenUniversityDefaultUsefulLinks() {
        return studentOpenUniversityDefaultUsefulLinks;
    }

    public List<Map<String, String>> getTeacherOpenUniversityDefaultUsefulLinks() {
        return teacherOpenUniversityDefaultUsefulLinks;
    }

    public void setTeacherOpenUniversityDefaultUsefulLinks(List<Map<String, String>>
                                                               teacherOpenUniversityDefaultUsefulLinks) {
        this.teacherOpenUniversityDefaultUsefulLinks = teacherOpenUniversityDefaultUsefulLinks;
    }
}
