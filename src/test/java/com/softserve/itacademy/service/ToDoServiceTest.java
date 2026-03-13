package com.softserve.itacademy.service;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToDoServiceTest {

    @Mock
    private ToDoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ToDoService todoService;

    @Test
    void create_ShouldReturnSavedToDo_WhenUniqueTitle() {
        ToDo todo = new ToDo();
        todo.setTitle("My ToDo");

        when(todoRepository.existsByTitle("My ToDo")).thenReturn(false);
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        ToDo created = todoService.create(todo);

        assertEquals(todo, created);
        verify(todoRepository).save(todo);
    }

    @Test
    void create_ShouldThrow_WhenTitleExists() {
        ToDo todo = new ToDo();
        todo.setTitle("Existing");

        when(todoRepository.existsByTitle("Existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> todoService.create(todo));
        verify(todoRepository, never()).save(any(ToDo.class));
    }

    @Test
    void addCollaborator_ShouldAddUserToSet() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        User collaborator = new User();
        collaborator.setEmail("collab@mail.com");

        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Todo");
        todo.setOwner(owner);
        todo.setCollaborators(new HashSet<>());

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.findById(2L)).thenReturn(Optional.of(collaborator));
        when(todoRepository.existsByTitleAndIdNot("Todo", 1L)).thenReturn(false);
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        todoService.addCollaborator(1L, 2L);

        assertTrue(todo.getCollaborators().contains(collaborator));
        verify(todoRepository, atLeastOnce()).save(todo);
    }

    @Test
    void removeCollaborator_ShouldRemoveUserFromSet() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        User collaborator = new User();
        collaborator.setEmail("collab@mail.com");

        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Todo");
        todo.setOwner(owner);
        todo.setCollaborators(new HashSet<>());
        todo.getCollaborators().add(collaborator);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.findById(2L)).thenReturn(Optional.of(collaborator));
        when(todoRepository.existsByTitleAndIdNot("Todo", 1L)).thenReturn(false);
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        todoService.removeCollaborator(1L, 2L);

        assertFalse(todo.getCollaborators().contains(collaborator));
        verify(todoRepository, atLeastOnce()).save(todo);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.readById(1L));
    }
}
