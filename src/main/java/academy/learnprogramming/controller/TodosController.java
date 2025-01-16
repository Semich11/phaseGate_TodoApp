package academy.learnprogramming.controller;

import academy.learnprogramming.dto.request.TodoRequestDto;
import academy.learnprogramming.dto.response.ApiResponse;
import academy.learnprogramming.dto.response.TodoResponseDto;
import academy.learnprogramming.service.TodoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
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

    @DeleteMapping("/get/{text}")
    public ResponseEntity<?> getTest(@PathVariable("text") String text){
        try {
            return new ResponseEntity<>(new ApiResponse(true, text), OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), BAD_REQUEST);
        }
    }

}

