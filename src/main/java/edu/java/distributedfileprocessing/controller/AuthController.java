package edu.java.distributedfileprocessing.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Контроллер для регистрации и авторизации пользователей.
 */
@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    @GetMapping("/login")
    public void logIn(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("/oauth2/authorization/google");
    }

}
