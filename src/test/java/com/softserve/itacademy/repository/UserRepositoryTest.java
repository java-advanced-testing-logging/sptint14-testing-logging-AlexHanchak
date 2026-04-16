package com.softserve.itacademy.repository;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.sql.init.mode=never")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setEmail("ivanov@mail.com");
        user.setPassword("{noop}pass123");
        user.setRole(UserRole.USER);
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("ivanov@mail.com");

        assertTrue(found.isPresent());
        assertEquals("Ivanov", found.get().getLastName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userRepository.findByEmail("absent@mail.com");
        assertTrue(found.isEmpty());
    }
}
