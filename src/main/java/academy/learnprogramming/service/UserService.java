package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.data.repository.TokenRepository;
import academy.learnprogramming.data.repository.UserRepository;
import academy.learnprogramming.dto.request.UserRequestDto;
import academy.learnprogramming.mapper.UserMapper;
import academy.learnprogramming.validators.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private  UserRepository userRepository;

    private final  ObjectValidator<Users> userValidator;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserMapper userMapper;


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public String registerUser(UserRequestDto userRequestDto) {
        System.out.println("\n\n\n\n\n\n\n " + userRequestDto + "\n\n\n\n\n\n\n\n");

        Users user = userMapper.toUserEntity(userRequestDto);

        userValidator.validate(user);

        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println("\n\n\n\n\n\n\n " + "register a user" + "\n\n\n\n\n\n\n\n");

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Registration failed. Please try again later.");
        }
        String jwtCookie = jwtService.generateJwtCookie(user.getEmail()).toString();
        System.out.println("\n\n\n\n\n\n\n " + jwtCookie + "\n\n\n\n\n\n\n\n");
        userRepository.save(user);
//        saveUserToken(user, jwtCookie);
//        addTokenToUser(user, jwtCookie);
        return jwtCookie;
    }

    public String verify(UserRequestDto userRequestDto) {
        System.out.println("\n\n\n\n\n\n\n " + "Authentication started: " + "\n\n\n\n\n\n\n\n");

        Users existingUser = userRepository.findByEmail(userRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("\n\n\n\n\n\n\n\nUser not found in verify\n\n\n\n\n\n\n"));
        ;

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(existingUser.getEmail(), userRequestDto.getPassword()));
        System.out.println("\n\n\n\n\n\n\n " + "User Is authenticated!: " + "\n\n\n\n\n\n\n\n");


        if (authentication.isAuthenticated()) {
            String jwtCookie =  jwtService.generateJwtCookie(existingUser.getEmail()).toString();
//            revokeAllUserToken(existingUser);
//            Token token = saveUserToken(existingUser, jwtCookie);
//            addTokenToUser(existingUser, token.getToken());
            return jwtCookie;
        }
        return "Fail";
    }

    private void revokeAllUserToken(Users existingUser) {
        List<Token> validTokenListByUser = tokenRepository.findAllValidTokenByUserId(existingUser.getId());
        if(validTokenListByUser.isEmpty())
            return;
        validTokenListByUser.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validTokenListByUser);
    }

    private Token saveUserToken(Users user, String jwtToken) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setUserId(user.getId());
        token.setRevoked(false);
        token.setExpired(false);
        System.out.println("User ID: " + token.getUserId());
        System.out.println("User Tokens Before: " + user.getTokens());
        System.out.println("Saving token: " + token);
        tokenRepository.save(token);
        System.out.println("Token saved.");
        return token;

    }

    private void addTokenToUser(Users user, String jwtToken) {
        user = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<String> tokenIds = user.getTokens();
        tokenIds.add(jwtToken);
        user.setTokens(tokenIds);
        userRepository.save(user);
        System.out.println("\n\n\n\n\n\n\nUser Tokens After: " + user.getTokens() + "\n\n\n\n\n\n\n\n");

    }
}
