package fi.helsinki.opintoni.integration.studyregistry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyAttainment {
    public List<Teacher> teachers = new ArrayList<>();
    public List<LocalizedText> grade = new ArrayList<>();
    public Integer credits;
    public Long studyAttainmentId;
    public List<LocalizedText> learningOpportunityName = new ArrayList<>();
    public LocalDateTime attainmentDate;
}
