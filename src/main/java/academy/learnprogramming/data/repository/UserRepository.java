package academy.learnprogramming.data.repository;

import academy.learnprogramming.data.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {

    Users findByEmail(String email);

    Optional<Users> findByUsername(String username);
}
