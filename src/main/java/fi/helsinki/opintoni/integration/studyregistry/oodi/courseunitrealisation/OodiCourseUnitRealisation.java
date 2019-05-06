package fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.helsinki.opintoni.util.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class OodiCourseUnitRealisation {
    @JsonProperty("learningopportunity_id")
    public String learningOpportunityId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonProperty("course_id")
    public String realisationId;

    @JsonProperty("parent_id")
    public String parentId;

    @JsonProperty("cancelled")
    public boolean isCancelled;

    @JsonProperty("position")
    public String position;
}
