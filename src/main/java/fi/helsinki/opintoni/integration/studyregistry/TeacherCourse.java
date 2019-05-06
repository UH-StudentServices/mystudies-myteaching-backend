package fi.helsinki.opintoni.integration.studyregistry;

import com.google.common.collect.Lists;

import java.util.List;

public class TeacherCourse extends CourseRealisation {
    public List<LocalizedText> realisationName = Lists.newArrayList();

    public String webOodiUri;

    public Integer realisationTypeCode;

    public String rootId;

    public List<LocalizedText> realisationRootName = Lists.newArrayList();

    public String teacherRole;
}
