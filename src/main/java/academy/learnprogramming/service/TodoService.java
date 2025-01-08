package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Todo;
import academy.learnprogramming.dto.request.TodoRequestDto;
import academy.learnprogramming.dto.response.TodoResponseDto;

import java.util.List;

public interface TodoService {
    public TodoResponseDto createTodo(TodoRequestDto todoRequest);
    List<TodoResponseDto> findAllTodo();

    List<TodoResponseDto> deleteATodoById(String todoId);
}
