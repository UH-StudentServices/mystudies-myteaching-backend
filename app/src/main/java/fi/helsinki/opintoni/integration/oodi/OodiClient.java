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

package fi.helsinki.opintoni.integration.oodi;

import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;

import java.util.List;

public interface OodiClient {

    List<OodiEnrollment> getEnrollments(String studentNumber);

    List<OodiEvent> getStudentEvents(String studentNumber);

    List<OodiEvent> getTeacherEvents(String teacherNumber);

    List<OodiStudyAttainment> getStudyAttainments(String studentNumber);

    List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString);

    List<OodiStudyRight> getStudentStudyRights(String studentNumber);

    List<OodiCourseUnitRealisationTeacher> getCourseUnitRealisationTeachers(String realisationId);

    OodiStudentInfo getStudentInfo(String studentNumber);

    OodiRoles getRoles(String oodiPersonId);

    OodiLearningOpportunity getLearningOpportunity(String learningOpportunityId);
}
