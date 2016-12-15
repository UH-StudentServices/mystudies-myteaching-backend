package fi.helsinki.opintoni.web.arguments;

public enum PortfolioRole {

    STUDENT("student"),
    TEACHER("teacher");

    private final String role;

    PortfolioRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static PortfolioRole fromValue(String role) {
        for (PortfolioRole portfolioRole : PortfolioRole.values()) {
            if(portfolioRole.role.equals(role)){
                return portfolioRole;
            }
        }
        throw new IllegalArgumentException("No matching PortfolioRole for " + role);
    }
}
