//package academy.learnprogramming;
//
//import academy.learnprogramming.data.model.Todo;
//import academy.learnprogramming.data.model.UserPrincipal;
//import academy.learnprogramming.data.model.Users;
//import academy.learnprogramming.data.repository.TodoRepository;
//import academy.learnprogramming.service.TodoServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TodoServiceTest {
//    @Mock
//    private TodoRepository todoRepository;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    @Mock
//    private Authentication authentication;
//
//    @Mock
//    private UserPrincipal userPrincipal;
//
//    @InjectMocks
//    private TodoServiceImpl todoServiceImpl;
//
//    private Users testUser;
//    private Todo todo;
//    private Todo todo2;
//
//
//    @BeforeEach
//    void setUp() {
//        testUser = new Users();
//        testUser.setId("generatedUserId");
//        testUser.setUsername("testUser");
//        testUser.setPassword("password");
//
//
//
//
//        SecurityContextHolder.setContext(securityContext);
//
//        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
//        lenient().when(authentication.getPrincipal()).thenReturn(userPrincipal);
//        lenient().when(userPrincipal.getId()).thenReturn(testUser.getId());
//
//    }
//
//    @Test
//    public void testThatUserCanCreateATodo(){
//        todo = new Todo();
//        todo.setId("generatedId");
//        todo.setTitle("New Todo");
//        todo.setDescription("creating and testing a todo");
//        todo.setCreatedAt(new Date());
//        todo.setCompleted(false);
//
//        when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);
//        todoServiceImpl.createTodo(todo);
//
//        System.out.printf("\n\n\n\n\n\n\n %s \n\n\n\n\n\n\n\n", todo.getUserId());
//
//        verify(todoRepository, times(1)).save(Mockito.any(Todo.class));
//        verify(todoRepository).save(argThat(todoObject ->
//                todoObject.getTitle().equals("New Todo") &&
//                todoObject.getDescription().equals("creating and testing a todo") &&
//                !todoObject.isCompleted() &&
//                todoObject.getUserId().equals(testUser.getId())
//        ));
//
//    }
//
//
//    @Test
//    public void testThatUserCanGetAllTodo() {
//        todo = new Todo();
//        todo.setId("generatedId");
//        todo.setTitle("New Todo");
//        todo.setDescription("creating and testing a todo");
//        todo.setCreatedAt(new Date());
//        todo.setCompleted(false);
//
//        todo2 = new Todo();
//        todo2.setId("2");
//        todo2.setTitle("Todo 2");
//        todo2.setDescription("Description for Todo 2");
//        todo2.setCompleted(true);
//
//        List<Todo> todos = new ArrayList<>(List.of(todo, todo2));
//        when(todoRepository.findAll()).thenReturn(todos);
//
//        List<Todo> allTodos = todoServiceImpl.findAllTodo();
//
//        assertNotNull(allTodos);
//        assertNotNull(allTodos);
//        assertEquals(2, allTodos.size());
//        assertTrue(allTodos.contains(todo));
//        assertTrue(allTodos.contains(todo2));
//    }
//
//    @Test
//    public void testThatUserCanUpdateAnExistingTodo() {
//        todo = new Todo();
//        todo.setId("generatedId2");
//        todo.setTitle("Initial Todo");
//        todo.setDescription("Initial description");
//        todo.setCreatedAt(new Date());
//        todo.setCompleted(false);
//
//
//        when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);
//        todoServiceImpl.createTodo(todo);
//        System.out.printf("\n\n\n\n\n\n\n %s \n\n\n\n\n\n\n\n", todo.getUserId());
//
//
//
//        verify(todoRepository, times(1)).save(Mockito.any(Todo.class));
//        verify(todoRepository).save(argThat(todoObject ->
//                todoObject.getTitle().equals("Initial Todo") &&
//                        todoObject.getDescription().equals("Initial description") &&
//                        !todoObject.isCompleted() &&
//                        todoObject.getUserId().equals(testUser.getId())
//        ));
//
//
//
//        Todo updatedTodo = new Todo();
//        updatedTodo.setId("generatedId2");
//        updatedTodo.setTitle("Updated Todo");
//        updatedTodo.setDescription("Updated description");
//        updatedTodo.setCompleted(true);
//
//        when(todoRepository.findById("generatedId2")).thenReturn(java.util.Optional.of(todo));
//        todoServiceImpl.createTodo(updatedTodo);
//
//        verify(todoRepository, times(2)).save(argThat(savedTodo ->
//                savedTodo.getId().equals("generatedId2") &&
//                        savedTodo.getTitle().equals("Updated Todo") &&
//                        savedTodo.getDescription().equals("Updated description") &&
//                        savedTodo.isCompleted() &&
//                        savedTodo.getUserId().equals(testUser.getId())
//        ));
//
//        verify(todoRepository, times(2)).findById("generatedId2");
//
//    }
//
//    @Test
//    public void testThatUserCanDeleteATodoById(){
//        todo2 = new Todo();
//        todo2.setId("generatedId");
//        todo2.setTitle("New Todo");
//        todo2.setDescription("creating and testing a todo");
//        todo2.setCreatedAt(new Date());
//        todo2.setCompleted(false);
//
//        todo = new Todo();
//        todo.setId("generatedId2");
//        todo.setTitle("Initial Todo");
//        todo.setDescription("Initial description");
//        todo.setCreatedAt(new Date());
//        todo.setCompleted(false);
//
//
//        List<Todo> todos = new ArrayList<>(List.of(todo, todo2));
//
//        when(todoRepository.findAll()).thenReturn(todos);
//        when(todoRepository.existsById(todo.getId())).thenReturn(true);
//
//        doAnswer(invocation -> {
//            String id = invocation.getArgument(0);
//            todos.removeIf(todo -> todo.getId().equals(id));
//            return null;
//        }).when(todoRepository).deleteById(todo.getId());
//
//        List<Todo> allTodos = todoServiceImpl.deleteATodoById(todo.getId());
//
//        verify(todoRepository, times(1)).existsById(todo.getId());
//        verify(todoRepository, times(1)).deleteById(todo.getId());
//
//        assertNotNull(allTodos);
//        assertNotNull(allTodos);
//        assertEquals(1, allTodos.size());
//        assertFalse(allTodos.contains(todo));
//        assertTrue(allTodos.contains(todo2));
//
//    }
//
//    @Test
//    public void testThatDeleteATodoByIdThrowsRuntimeException(){
//        String id = "1";
//
//        when(todoRepository.existsById(id)).thenReturn(false);
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            todoServiceImpl.deleteATodoById(id);
//        });
//
//        assertEquals("Todo is not found", exception.getMessage());
//
//        verify(todoRepository, times(1)).existsById(id);
//        verify(todoRepository, never()).deleteById(id);
//    }
//
//    }
