package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Todo;
import academy.learnprogramming.data.model.UserPrincipal;
import academy.learnprogramming.data.repository.TodoRepository;
import academy.learnprogramming.dto.request.TodoRequestDto;
import academy.learnprogramming.dto.response.TodoResponseDto;
import academy.learnprogramming.mapper.TodoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService{
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    public TodoMapper todoMapper;

    @Override
    public TodoResponseDto createTodo(TodoRequestDto todoRequest) {
        Todo existingTodo = null;
        if (todoRequest.getId() != null) {
            existingTodo = todoRepository.findById(todoRequest.getId()).orElse(null);
        }
        if(existingTodo != null){
            return updateTodo(existingTodo, todoRequest);
        }
        return creatATodo(todoRequest);
    }

    @Override
    public List<TodoResponseDto> findAllTodo() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream()
                .map(todo -> todoMapper.toResponse(todo))
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponseDto> deleteATodoById(String todoId) {
        if (todoRepository.existsById(todoId)){
            todoRepository.deleteById(todoId);
            return findAllTodo();
        }
        else{
            throw new RuntimeException("Todo is not found");
        }

    }

private TodoResponseDto creatATodo(TodoRequestDto todoRequest) {
    Todo todo = todoMapper.toEntity(todoRequest);
    String userId = getCurrentUserId();
    todo.setUserId(userId);
    todoRepository.save(todo);
    return todoMapper.toResponse(todo);
}

    private TodoResponseDto updateTodo(Todo existingTodo, TodoRequestDto todoRequest) {
        existingTodo.setTitle(todoRequest.getTitle());
        existingTodo.setDescription(todoRequest.getDescription());
        existingTodo.setCompleted(todoRequest.isCompleted());
        existingTodo.setCreatedAt(new Date());
        todoRepository.save(existingTodo);
        return todoMapper.toResponse(existingTodo);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        throw new RuntimeException("User not authenticated");
    }

}

