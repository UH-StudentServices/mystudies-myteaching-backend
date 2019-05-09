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

package fi.helsinki.opintoni.integration.studyregistry.sisu;

import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AcceptorPerson;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Attainment;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Grade;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.GradeScale;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedString;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PublicPerson;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SisuStudyRegistryConverter {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public StudyAttainment convertAttainment(Attainment attainment) {
        StudyAttainment studyAttainment = new StudyAttainment();
        studyAttainment.attainmentDate = LocalDate
            .parse(attainment.attainmentDate, DateTimeFormatter.ofPattern(DATE_PATTERN))
            .atStartOfDay();
        studyAttainment.credits = attainment.credits.intValue();
        studyAttainment.grade = convertGrade(attainment.gradeScale, attainment.gradeId);
        studyAttainment.studyAttainmentId = sisuIDToLong(attainment.id);
        studyAttainment.learningOpportunityName = convertLocalizedString(attainment.courseUnit.name);
        studyAttainment.teachers = attainment.acceptorPersons != null ? attainment.acceptorPersons.stream()
            .map(this::convertAcceptorPerson)
            .collect(Collectors.toList())
            : new ArrayList<>();
        return studyAttainment;
    }

    public Long sisuIDToLong(String sisuID) {
        Long id;

        if (sisuID.contains("-")) {
            String[] parts = sisuID.split("-");
            String idPart = parts[parts.length - 1];
            id = Long.parseLong(idPart);
        } else {
            id = Long.parseLong(sisuID);
        }

        return id;
    }

    private Teacher convertAcceptorPerson(AcceptorPerson acceptorPerson) {
        Teacher teacher = new Teacher();
        PublicPerson person = acceptorPerson.person;
        String firstName = person.firstName != null ? person.firstName : "";
        String lastName = person.lastName != null ? person.lastName : "";
        teacher.name = String.format("%s %s", firstName, lastName);
        return teacher;
    }

    public List<LocalizedText> convertGrade(GradeScale scale, Integer gradeId) {
        Grade grade = scale.grades.stream()
            .filter((g) -> g.localId == gradeId)
            .findFirst()
            .orElseThrow();
        return convertLocalizedString(grade.name);
    }

    private List<LocalizedText> convertLocalizedString(LocalizedString localizedString) {
        return List.of(
            new LocalizedText(StudyRegistryLocale.FI, localizedString.fi),
            new LocalizedText(StudyRegistryLocale.SV, localizedString.sv),
            new LocalizedText(StudyRegistryLocale.EN, localizedString.en)
        );
    }

    public Person sisuPrivatePersonToPerson(PrivatePersonRequest privatePersonRequest) {
        Person person = new Person();
        person.studentNumber = privatePersonRequest.studentNumber;
        person.teacherNumber = privatePersonRequest.employeeNumber;
        return person;
    }
}
