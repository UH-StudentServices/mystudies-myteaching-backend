package fi.helsinki.opintoni.service.feedback;

public enum FeedbackSite {
    STUDENT ("opintoni"),
    TEACHER ("opetukseni");

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
