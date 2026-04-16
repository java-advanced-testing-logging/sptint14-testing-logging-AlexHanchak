package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ToDoController.class)
public class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService todoService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private ToDoDtoConverter todoDtoConverter;

    @Test
    void getAll_ShouldReturnTodosForUser() throws Exception {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mail.com");

        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Todo");

        when(todoService.getByUserId(1L)).thenReturn(List.of(todo));
        when(userService.readById(1L)).thenReturn(owner);

        mockMvc.perform(get("/todos/all/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getTasks_ShouldReturnTasksAndPotentialCollaborators() throws Exception {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mail.com");

        User collaborator = new User();
        collaborator.setId(2L);
        collaborator.setEmail("collab@mail.com");

        User candidate = new User();
        candidate.setId(3L);
        candidate.setEmail("candidate@mail.com");

        State state = new State();
        state.setId(5L);
        state.setName("New");

        Task task = new Task();
        task.setId(10L);
        task.setState(state);

        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setOwner(owner);
        todo.setTasks(Set.of(task));
        todo.setCollaborators(new HashSet<>());
        todo.getCollaborators().add(collaborator);

        when(todoService.readById(1L)).thenReturn(todo);
        when(userService.getAll()).thenReturn(Arrays.asList(owner, collaborator, candidate));

        mockMvc.perform(get("/todos/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("users"))
                .andExpect(result -> {
                    List<User> users = (List<User>) result.getModelAndView().getModel().get("users");
                    assertEquals(1, users.size());
                    assertEquals("candidate@mail.com", users.get(0).getEmail());
                });
    }

    @Test
    void createToDo_ShouldReturnForm_WhenDuplicateTitle() throws Exception {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mail.com");

        ToDo todo = new ToDo();
        todo.setTitle("Duplicate");
        todo.setOwner(owner);

        when(userService.readById(1L)).thenReturn(owner);
        when(todoDtoConverter.toEntity(any(), any())).thenReturn(todo);
        when(todoService.create(any())).thenThrow(new IllegalArgumentException("ToDo with title already exists"));

        mockMvc.perform(post("/todos/create/users/1")
                        .param("title", "Duplicate")
                        .param("ownerId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"))
                .andExpect(model().attributeHasFieldErrors("todo", "title"));
    }

    @Test
    void getTasks_ShouldReturn404View_WhenToDoNotFound() throws Exception {
        when(todoService.readById(999L)).thenThrow(new EntityNotFoundException("ToDo not found"));

        mockMvc.perform(get("/todos/999/tasks"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }
}
