package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDtoConverter userDtoConverter;

    @Test
    void createGet_ShouldReturnCreateUserView() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void updateGet_ShouldReturnUpdateUserView() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setEmail("ivan@mail.com");
        user.setRole(UserRole.USER);

        when(userService.readById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    void createPost_ShouldRedirect_WhenValid() throws Exception {
        User user = new User();
        user.setId(1L);

        when(userService.register(any())).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("email", "ivan@mail.com")
                        .param("password", "pass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    void createPost_ShouldReturnForm_WhenInvalid() throws Exception {
        mockMvc.perform(post("/users/create")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "bad")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));
    }

    @Test
    void getAll_ShouldReturnUsersListView() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"));
    }
}
