package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.data.repository.TokenRepository;
import academy.learnprogramming.data.repository.UserRepository;
import academy.learnprogramming.validators.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public String registerUser(Users user) {
        userValidator.validate(user);
        user.setPassword(encoder.encode(user.getPassword()));

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Registration failed. Please try again later.");
        }
        String jwtToken = jwtService.generateToken(user.getUsername());
        userRepository.save(user);
        saveUserToken(user, jwtToken);
        addTokenToUser(user, jwtToken);
        return jwtToken;
    }

    public String verify(Users user) {

        Users existingUser = userRepository.findByEmail(user.getEmail());

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(existingUser.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            String jwtToken =  jwtService.generateToken(existingUser.getUsername());
            revokeAllUserToken(existingUser);
            Token token = saveUserToken(existingUser, jwtToken);
            addTokenToUser(existingUser, token.getToken());
            return jwtToken;
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


    public String throwException() {
        throw new IllegalStateException("nothing to throw");
    }
}
