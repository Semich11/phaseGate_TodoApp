package academy.learnprogramming.mapper;



import academy.learnprogramming.dto.request.UserRequestDto;
import academy.learnprogramming.dto.response.UserResponseDto;
import academy.learnprogramming.data.model.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public Users toUserEntity(UserRequestDto request) {
        Users user = new Users();
        user.setUsername(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    public UserResponseDto toUserResponse(Users user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        return response;
    }
}
