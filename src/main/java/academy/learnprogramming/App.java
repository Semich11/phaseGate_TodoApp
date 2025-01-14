package academy.learnprogramming;

import academy.learnprogramming.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App
{
    @Autowired
    private UserRepository userRepository;

    public static void main( String[] args ){
        SpringApplication.run(App.class, args);
    }


    @Bean
    public CommandLineRunner testMongoConnection() {
        return args -> {
            System.out.println("\n\n\n\n\n\n\n\n\nTesting MongoDB connection...\n\n\n\n\n\n\n");

            userRepository.findAll().forEach(user -> {
                System.out.println("Found user: " + user.getUsername());
                System.out.println("Found password: " + user.getPassword());
                System.out.println("Found email: " + user.getEmail());

            });
        };
    }

}
