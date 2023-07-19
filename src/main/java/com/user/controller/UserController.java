package com.user.controller;

import com.user.entities.User;
import com.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @GetMapping("/{userId}")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "getUserByIdFallback")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    public ResponseEntity<User> getUserByIdFallback(String id, Exception ex) {
        User user = User.builder().name("dummy").email("dummy@dummy.com").about("This is dummy because Rating service" +
                                                                                        " is down").build();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
