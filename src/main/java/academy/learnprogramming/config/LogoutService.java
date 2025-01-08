package academy.learnprogramming.config;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
