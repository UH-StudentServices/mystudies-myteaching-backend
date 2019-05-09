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

package fi.helsinki.opintoni.integration.studyregistry.sisu.mock;

import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuClient;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Attainment;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnit;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Grade;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeScale;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedString;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyAttainmentRequest;

import java.util.List;

public class SisuMockClient implements SisuClient {

    @Override
    public PrivatePersonRequest getPrivatePerson(String id) {
        PrivatePersonRequest privatePersonRequest = new PrivatePersonRequest();
        privatePersonRequest.studentNumber = "010189791";
        privatePersonRequest.employeeNumber = "010540";
        return privatePersonRequest;
    }

    @Override
    public StudyAttainmentRequest getStudyAttainments(String id) {
        Attainment attainment = new Attainment();
        attainment.id = "hy-opinto-126377006";
        attainment.credits = 5.0;
        attainment.attainmentDate = "2019-05-09";
        attainment.gradeScale = getGradeScale();
        attainment.courseUnit = getCourseUnit();
        StudyAttainmentRequest studyAttainmentRequest = new StudyAttainmentRequest();
        studyAttainmentRequest.attainments = List.of(attainment);
        return studyAttainmentRequest;
    }

    private CourseUnit getCourseUnit() {
        CourseUnit courseUnit = new CourseUnit();
        courseUnit.name = getLocalizedString("Integraalilaskenta", "Integral Calculus", "Integral kalkyl");
        return courseUnit;
    }

    private GradeScale getGradeScale() {
        GradeScale gradeScale = new GradeScale();
        Grade grade0 = getGrade(0, "hylätty", "fail", "underkänd");
        Grade grade1 = getGrade(1, "välttävä", "passable", "försvarlig");
        Grade grade2 = getGrade(2, "tyydyttävä", "satisfactory", "nöjaktig");
        Grade grade3 = getGrade(3, "hyvä", "good", "god");
        Grade grade4 = getGrade(4, "kiitettävä", "very good", "berömlig");
        Grade grade5 = getGrade(4, "erinomainen", "excellent", "utmärkt");
        gradeScale.grades = List.of(grade0, grade1, grade2, grade3, grade4, grade5);
        return gradeScale;
    }

    private Grade getGrade(int id, String fi, String en, String sv) {
        Grade grade = new Grade();
        grade.localId = id;
        grade.name = getLocalizedString(fi, en, sv);
        return grade;
    }

    private LocalizedString getLocalizedString(String fi, String en, String sv) {
        LocalizedString localizedString = new LocalizedString();
        localizedString.fi = fi;
        localizedString.sv = sv;
        localizedString.en = en;
        return localizedString;
    }
}
