package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Priyanshu Mishra
 */

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser() {
        User user = new User("User", "user@example.com", "hash", Role.USER);
        userRepository.save(user);

        assertTrue(userRepository.findByEmail("user@example.com").isPresent());
    }

    @Test
    void existsByEmail_returnsTrueWhenPresent() {
        User user = new User("User", "exists@example.com", "hash", Role.USER);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@example.com"));
    }
}
