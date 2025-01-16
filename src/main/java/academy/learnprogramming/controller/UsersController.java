package academy.learnprogramming.controller;

import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.dto.request.UserRequestDto;
import academy.learnprogramming.dto.response.ApiResponse;
import academy.learnprogramming.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
            @RequestBody UserRequestDto userRequestDto
    ) {
        System.out.println("\n\n\n\n\n\n\n " + "jwtToken" + "\n\n\n\n\n\n\n\n");
        try{
//            return new ResponseEntity<>(new ApiResponse(true, userService.registerUser(userRequestDto)), OK);
            String jwtCookie= userService.registerUser(userRequestDto);
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.SET_COOKIE,
                            jwtCookie
                    )
                    .body(new ApiResponse(true, jwtCookie));
        }catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto userRequestDto) {
        try{
//            return new ResponseEntity<>(new ApiResponse(true, userService.verify(userRequestDto)), OK);
            String jwtCookie= userService.verify(userRequestDto);
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.SET_COOKIE,
                            jwtCookie
                    )
                    .body(new ApiResponse(true, jwtCookie));
        }catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), BAD_REQUEST);
        }
    }
}
