package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserCleanerServiceTest extends SpringTest {

    @Autowired
    private UserCleanerService userCleanerService;

    @Test
    public void thatReturnsInactiveUsers() {
        List<User> inactiveUsers = userCleanerService.findInactiveUsers();
        assertThat(inactiveUsers.isEmpty()).isFalse();
    }

}
