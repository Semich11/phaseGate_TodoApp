package academy.learnprogramming.controller;

import academy.learnprogramming.data.model.Todo;
import academy.learnprogramming.dto.request.TodoRequestDto;
import academy.learnprogramming.dto.response.ApiResponse;
import academy.learnprogramming.dto.response.TodoResponseDto;
import academy.learnprogramming.service.TodoServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class TodosController {
    @Autowired
    private TodoServiceImpl todoServiceImpl;


    @PostMapping("/todos")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDto todoRequest){
        try {
            TodoResponseDto responseDto = todoServiceImpl.createTodo(todoRequest);
            return new ResponseEntity<>(new ApiResponse(true, responseDto), OK);
        }catch(Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/getAllTodo")
    public List<TodoResponseDto> getAllTodo(){
        return todoServiceImpl.findAllTodo();
    }

//    @DeleteMapping("/{id}")
//    public List<TodoResponseDto> deleteTodo(@PathVariable String id){
//        return todoServiceImpl.deleteATodoById(id);
//    }
}

