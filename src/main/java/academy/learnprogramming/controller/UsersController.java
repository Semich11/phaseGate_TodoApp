package academy.learnprogramming.controller;

import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.dto.response.ApiResponse;
import academy.learnprogramming.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;

@RestController
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String greet(HttpServletRequest request) {
        return "Welcome to Christopher " + request.getSession().getId();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Users user
    ) {
        return new ResponseEntity<>(new ApiResponse(true, userService.registerUser(user)), OK);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        System.out.println(user);
        return userService.verify(user);
    }

    @GetMapping("/error")
    public ResponseEntity<?> throwException() {
        return new ResponseEntity<>(new ApiResponse(true, userService.throwException()), OK);
    }


}
