package fi.helsinki.opintoni.integration.studyregistry;

import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.List;

public class CourseRealisation {
    public List<LocalizedText> name = Lists.newArrayList();

    public String learningOpportunityId;

    public LocalDateTime endDate;

    public LocalDateTime startDate;

    public String realisationId;

    public String parentId;

    public boolean isCancelled;

    public String position;
}
