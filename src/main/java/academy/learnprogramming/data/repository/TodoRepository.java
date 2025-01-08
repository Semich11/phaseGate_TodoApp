package academy.learnprogramming.data.repository;

import academy.learnprogramming.data.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoRepository extends MongoRepository<Todo, String> {
}
