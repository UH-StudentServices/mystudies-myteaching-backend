package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.repository.UserRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCleanerService {

    private static final Logger log = LoggerFactory.getLogger(UserCleanerService.class);

    private UserRepository userRepository;

    @Autowired
    public UserCleanerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void cleanInactiveUsers() {
        List<User> inactiveUsers = findInactiveUsers();

    }

    public List<User> findInactiveUsers() {
        return userRepository.findInactiveUsers(DateTime.now().minusYears(1), User.AccountStatus.ACTIVE, DateTime.now());
    }

    private void updateAccountActiveUntilDateForUsers() {

    }
}
