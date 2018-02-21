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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.Position;
import fi.helsinki.opintoni.integration.sisu.*;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OodiSisuClient implements OodiClient {
    private static final String EXAM_ASSESSMENT_ITEM_TYPE = "urn:code:assessment-item-type:exam";
    private static final String COURSE_REALISATION_ID_PREFIX = "hy-CUR-";
    private static final Integer TYPE_LECTURE_COURSE = 5;

    private final SisuRestClient sisuRestClient;
    private final String sisuPersonId;
    private final Integer examTypeCode;

    public OodiSisuClient(SisuRestClient sisuRestClient, Environment environment) {
        this.sisuRestClient = sisuRestClient;
        this.sisuPersonId = environment.getProperty("testSisuPersonId");
        this.examTypeCode = Integer.valueOf( (String) (environment.getProperty("courses.examTypeCodes", List.class).get(0)));
    }

    @Override
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        List<Enrolment> enrollments = sisuRestClient.getEnrolments(sisuPersonId);

        return enrollments.stream()
            .map(this::sisuEnrolmentToOodiEnrollment)
            .collect(Collectors.toList());
    }

    @Override
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiEvent> getTeacherEvents(String teacherNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber) {
        return new ArrayList<>();
    }

    @Override
    public List<OodiCourseUnitRealisationTeacher> getCourseUnitRealisationTeachers(String realisationId) {
        return new ArrayList<>();
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return null;
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return new OodiRoles();
    }

    @Override
    public OodiLearningOpportunity getLearningOpportunity(String learningOpportunityId) {
        return new OodiLearningOpportunity(); //May not be needed anymore because Course recommendations have been removed?
    }

    private OodiEnrollment sisuEnrolmentToOodiEnrollment(Enrolment enrolment) {
        OodiEnrollment oodiEnrollment = new OodiEnrollment();

        CourseUnitRealisation courseUnitRealisation = sisuRestClient.getCourseUnitRealisation(enrolment.courseUnitRealisationId);
        Assessment assessment = sisuRestClient.getAssessment(enrolment.assessmentItemId);

        oodiEnrollment.name = sisuLocalizedStringToOodiLocalizedValueList(courseUnitRealisation.name);
        oodiEnrollment.learningOpportunityId = enrolment.courseUnitId;

        if(isExam(assessment)) {
            oodiEnrollment.typeCode = examTypeCode;
            //start and end date for exam???
        } else {
            oodiEnrollment.typeCode = TYPE_LECTURE_COURSE;
            oodiEnrollment.startDate = courseUnitRealisation.activityPeriod.startDate.atStartOfDay();
            oodiEnrollment.endDate = courseUnitRealisation.activityPeriod.startDate.atStartOfDay();
        }

        oodiEnrollment.realisationId = enrolment.courseUnitRealisationId.replace(COURSE_REALISATION_ID_PREFIX, "");
        oodiEnrollment.position = Position.ROOT.getValue();
        oodiEnrollment.credits = assessment.credits.max;

        return oodiEnrollment;

    }

    private List<OodiLocalizedValue> sisuLocalizedStringToOodiLocalizedValueList(LocalizedString localizedString) {

        return Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, localizedString.fi),
            new OodiLocalizedValue(OodiLocale.SV, localizedString.sv),
            new OodiLocalizedValue(OodiLocale.EN, localizedString.en));
    }

    private boolean isExam(Assessment assessment) {
       return EXAM_ASSESSMENT_ITEM_TYPE.equals(assessment.assessmentItemType);
    }
}
