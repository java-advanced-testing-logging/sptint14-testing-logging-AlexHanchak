package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserDtoConverter userDtoConverter = new UserDtoConverter();

    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldSetRoleAndPasswordPrefix() {
        CreateUserDto dto = new CreateUserDto();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setEmail("ivan@mail.com");
        dto.setPassword("pass123");
        dto.setRole(UserRole.ADMIN);

        when(userRepository.findByEmail("ivan@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.register(dto);

        assertEquals(UserRole.USER, created.getRole());
        assertTrue(created.getPassword().startsWith("{noop}"));
        assertEquals("{noop}pass123", created.getPassword());
        verify(userRepository).findByEmail("ivan@mail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenDuplicateEmail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setEmail("dup@mail.com");
        dto.setPassword("pass123");

        when(userRepository.findByEmail("dup@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_ShouldAllowRoleChange_ForAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);
        user.setEmail("admin@mail.com");
        user.setFirstName("Admin");
        user.setLastName("User");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setFirstName("Admin2");
        dto.setLastName("User2");
        dto.setEmail("admin2@mail.com");
        dto.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updated = userService.update(dto);

        assertEquals(UserRole.USER, updated.getRole());
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("Admin2", user.getFirstName());
        assertEquals("User2", user.getLastName());
        assertEquals("admin2@mail.com", user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void update_ShouldNotAllowRoleChange_ForNonAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
        user.setEmail("user@mail.com");
        user.setFirstName("User");
        user.setLastName("One");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setFirstName("User2");
        dto.setLastName("One2");
        dto.setEmail("user2@mail.com");
        dto.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updated = userService.update(dto);

        assertEquals(UserRole.USER, updated.getRole());
        assertEquals(UserRole.USER, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void readById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.readById(1L);

        assertEquals(user, found);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.readById(1L));
    }
}
