package fi.helsinki.opintoni.integration.sisu;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SisuStudyRegistryTest extends SpringTest {

    @Autowired
    @Qualifier("sisuStudyRegistry")
    private StudyRegistry sisuStudyRegistry;

    @Test
    public void shouldMapValuesCorrectly() {
        Person person = sisuStudyRegistry.getPerson("FooBar");
        assertThat(person.teacherNumber).isEqualTo("220272");
        assertThat(person.studentNumber).isEqualTo("012023965");
    }
}
