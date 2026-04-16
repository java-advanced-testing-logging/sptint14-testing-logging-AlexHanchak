package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        log.info("GET / or /home");
        model.addAttribute("users", userService.getAll());

        return "home";
    }
}
