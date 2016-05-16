package fi.helsinki.opintoni.dto.portfolio;

public class CourseMaterialDto {

    public final CourseMaterialType courseMaterialType;
    public final String courseMaterialUri;

    public CourseMaterialDto(String courseMaterialUri, CourseMaterialType courseMaterialType) {
        this.courseMaterialUri = courseMaterialUri;
        this.courseMaterialType = courseMaterialType;
    }

    public enum CourseMaterialType {
        COURSE_PAGE, MOODLE
    }

}
