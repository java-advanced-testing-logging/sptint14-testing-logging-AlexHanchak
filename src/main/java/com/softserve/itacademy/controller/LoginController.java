package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@Slf4j
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        log.info("GET /login");
        if (session.getAttribute("user_id") != null) {
            log.debug("User is already authenticated, redirecting to home");
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            HttpSession session
    ) {
        log.info("POST /login for email: {}", email);
        var userOpt = userService.findByUsername(email);
        if (userOpt.isEmpty()) {
            log.warn("Login failed: user not found for email {}", email);
            return "redirect:/login?error=true";
        }
        var user = userOpt.get();
        if (user.getPassword().equals("{noop}" + password)) {
            session.setAttribute("username", user.getFirstName());
            session.setAttribute("user_id", user.getId());
            log.info("Login successful for user ID: {}", user.getId());
            return "redirect:/";
        } else {
            log.warn("Login failed: invalid password for email {}", email);
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        log.info("POST /logout");
        session.invalidate();
        log.debug("Session invalidated");
        return "redirect:/login?logout=true";
    }
}
