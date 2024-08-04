package edu.java.distributedfileprocessing.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для регистрации и авторизации пользователей.
 */
@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<?> signUp() {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn() {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut() {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
