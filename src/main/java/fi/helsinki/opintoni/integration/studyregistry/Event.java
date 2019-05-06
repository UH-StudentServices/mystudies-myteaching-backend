package fi.helsinki.opintoni.integration.studyregistry;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OptimeExtras;

import java.time.LocalDateTime;
import java.util.List;

public class Event {
    public String roomName;

    public Integer typeCode;

    public Integer realisationId;

    public List<LocalizedText> realisationName = Lists.newArrayList();

    public List<LocalizedText> realisationRootName = Lists.newArrayList();

    public LocalDateTime endDate;

    public LocalDateTime startDate;

    public String buildingStreet;

    public String buildingZipCode;

    public boolean isCancelled;

    public OptimeExtras optimeExtras;
}
