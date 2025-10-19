package com.example.ecomove.controller;

import com.example.ecomove.dto.LoginResponse;
import com.example.ecomove.models.User;
import com.example.ecomove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/addCredits")
    public ResponseEntity<Void> addCredits(@PathVariable String userId, @RequestBody Map<String, Integer> payload) {
    int credits = payload.getOrDefault("credits", 0);
    if (credits > 0) {
        userService.updateCreditPoints(userId, credits);
        return ResponseEntity.ok().build();
    } else {
        return ResponseEntity.badRequest().build();
    }
}

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }

@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");

    Optional<User> userOpt = userService.loginUser(email, password);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        String token = "mocked-token-" + user.getUserId(); // Replace with real JWT later

        LoginResponse response = new LoginResponse(user.getUserId(), user.getName(), token);
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

@PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        logger.info("Logging out token: {}", token);
        // Optional: Invalidate token if using a token store
    } else {
        logger.warn("Logout attempted without Authorization header");
    }

    return ResponseEntity.ok("Logout successful");
}


    @PutMapping("/{userId}/credit")
    public ResponseEntity<Void> updateCredit(@PathVariable String userId, @RequestParam int delta) {
        userService.updateCreditPoints(userId, delta);
        return ResponseEntity.ok().build();
    }

    
}