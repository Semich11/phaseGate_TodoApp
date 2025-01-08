package academy.learnprogramming.controller;

import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {
    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @GetMapping("/")
    public String greet(HttpServletRequest request) {
        return "Welcome to Christopher " + request.getSession().getId();
    }

    @PostMapping("/register")
    public String register(@RequestBody Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        System.out.println(user);
        return userService.verify(user);
    }
}
