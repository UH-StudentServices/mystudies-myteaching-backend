package fi.helsinki.opintoni.integration.sisu;

public enum CourseUnitRealisationResponsibilityInfoType {

    RESPONSIBLE_TEACHER("urn:code:course-unit-realisation-responsibility-info-type:responsible-teacher"),
    TEACHER("urn:code:course-unit-realisation-responsibility-info-type:teacher"),
    ADMINISTRATIVE_PERSON("urn:code:course-unit-realisation-responsibility-info-type:administrative-person"),
    CONTACT_INFO("urn:code:course-unit-realisation-responsibility-info-type:contact-info");

    private final String urn;

    CourseUnitRealisationResponsibilityInfoType(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }
}
