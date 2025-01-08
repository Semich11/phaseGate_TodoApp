package academy.learnprogramming.mapper;

import academy.learnprogramming.dto.request.TodoRequestDto;
import academy.learnprogramming.dto.response.TodoResponseDto;
import academy.learnprogramming.data.model.Todo;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequestDto userRequest) {
        Todo todo = new Todo();
        todo.setTitle(userRequest.getTitle());
        todo.setDescription(userRequest.getDescription());
        todo.setCreatedAt(new Date());
        todo.setCompleted(userRequest.isCompleted());
        return todo;
    }

    public TodoResponseDto toResponse(Todo todo) {
        TodoResponseDto response = new TodoResponseDto();
        response.setTitle(todo.getTitle());
        response.setCompleted(todo.isCompleted());
        response.setDescription(todo.getDescription());
        response.setCreatedAt(todo.getCreatedAt());
        return response;
    }
}
