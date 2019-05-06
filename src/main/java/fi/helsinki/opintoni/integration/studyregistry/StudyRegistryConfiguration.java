package fi.helsinki.opintoni.integration.studyregistry;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "studyregistry")
public class StudyRegistryConfiguration {
    private List<String> oodiDataSets = new ArrayList<>();

    private List<String> sisuDataSets = new ArrayList<>();

    public List<String> getOodiDataSets() {
        return oodiDataSets;
    }

    public List<String> getSisuDataSets() {
        return sisuDataSets;
    }
}
