package com.softserve.itacademy.repository;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.sql.init.mode=never")
public class ToDoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToDoRepository toDoRepository;

    @Test
    void getByUserId_ShouldReturnOwnedAndCollaborativeToDos() {
        User owner = createUser("Mike", "Brown", "owner@mail.com");
        User collaborator = createUser("Nora", "White", "collab@mail.com");

        ToDo owned = new ToDo();
        owned.setTitle("Owned todo");
        owned.setOwner(owner);
        owned.setCollaborators(new HashSet<>());
        entityManager.persist(owned);

        ToDo shared = new ToDo();
        shared.setTitle("Shared todo");
        shared.setOwner(owner);
        shared.setCollaborators(new HashSet<>());
        shared.getCollaborators().add(collaborator);
        entityManager.persist(shared);

        entityManager.flush();

        List<ToDo> todos = toDoRepository.getByUserId(collaborator.getId());

        assertEquals(1, todos.size());
        assertEquals("Shared todo", todos.get(0).getTitle());
    }

    @Test
    void existsByTitleAndExistsByTitleAndIdNot_ShouldWork() {
        User owner = createUser("Nick", "Green", "nick@mail.com");

        ToDo todo = new ToDo();
        todo.setTitle("Project list");
        todo.setOwner(owner);
        todo.setCollaborators(new HashSet<>());
        entityManager.persist(todo);
        entityManager.flush();

        assertTrue(toDoRepository.existsByTitle("Project list"));
        assertFalse(toDoRepository.existsByTitle("Unknown"));
        assertFalse(toDoRepository.existsByTitleAndIdNot("Project list", todo.getId()));
        assertTrue(toDoRepository.existsByTitleAndIdNot("Project list", -1L));
    }

    private User createUser(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("{noop}pass123");
        user.setRole(UserRole.USER);
        entityManager.persist(user);
        return user;
    }
}
