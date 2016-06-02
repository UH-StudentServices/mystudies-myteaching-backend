package fi.helsinki.opintoni.integration.unisport;

public class MockUnisportJWTService implements UnisportJWTService {

    public static final String MOCK_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyMDIwNDM4MTIifQ.qMawTWORtPkTjHcAdz_UPIvHrX2NPLzZxFWOQuus3co";

    @Override
    public String generateToken(Long unisportUserId) {
        return MOCK_JWT_TOKEN;
    }
}
