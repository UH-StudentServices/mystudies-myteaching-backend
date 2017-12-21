package fi.helsinki.opintoni.service.feedback;

public enum FeedbackSite {
    STUDENT ("myStudies"),
    TEACHER ("myTeaching"),
    PORTFOLIO ("portfolio"),
    ACADEMIC_PORTFOLIO ("academicPortfolio");

    private final String name;

    private FeedbackSite(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
