package academy.learnprogramming.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotNull(message = "The Username should not be empty!")
    @NotEmpty(message = "The Username should not be empty!")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotNull(message = "The Email should not be empty!")
    @NotEmpty(message = "The Email should not be empty!")
    @Email(message = "Invalid email address")
    private String email;

    @NotNull(message = "The Password should not be empty!")
    @NotEmpty(message = "The Password should not be empty!")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must have at least one digit, one lowercase, one uppercase, one special character, and be at least 8 characters long"
    )
    private String password;
}