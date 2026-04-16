package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @GetMapping("/create")
    public String create(Model model) {
        log.info("GET /users/create");
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") CreateUserDto userDto,
                        BindingResult result) {
        log.info("POST /users/create with email: {}", userDto.getEmail());
        if (result.hasErrors()) {
            log.warn("Validation errors while creating user: {}", result.getAllErrors().size());
            return "create-user";
        }
        try {
            User user = userService.register(userDto);
            log.info("User registered with ID: {}", user.getId());
            return "redirect:/todos/all/users/" + user.getId();
        } catch (IllegalArgumentException e) {
            log.warn("User registration failed: {}", e.getMessage());
            result.rejectValue("email", "error.user", e.getMessage());
            return "create-user";
        }
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable("id") Long id, Model model) {
        log.info("GET /users/{}/read", id);
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model) {
        log.info("GET /users/{}/update", id);
        User user = userService.readById(id);
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        
        model.addAttribute("user", userDto);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                        @Validated @ModelAttribute("user") UpdateUserDto userDto,
                        BindingResult result,
                        Model model,
                        HttpSession session) {
        log.info("POST /users/{}/update", id);
        if (result.hasErrors()) {
            log.warn("Validation errors while updating user {}: {}", id, result.getAllErrors().size());
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }
        userDto.setId(id);
        boolean canChangeRole = false;
        Object currentUserId = session.getAttribute("user_id");
        if (currentUserId instanceof Long currentId) {
            User currentUser = userService.readById(currentId);
            canChangeRole = currentUser.getRole() == UserRole.ADMIN;
        }
        userService.update(userDto, canChangeRole);
        return "redirect:/users/all";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        log.info("GET /users/{}/delete", id);
        userService.delete(id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        log.info("GET /users/all");
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found in UserController: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }
}
