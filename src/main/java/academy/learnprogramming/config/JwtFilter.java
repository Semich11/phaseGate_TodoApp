package academy.learnprogramming.config;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.repository.TokenRepository;
import academy.learnprogramming.service.JWTService;
import academy.learnprogramming.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        try {
            if (token != null) {
                username = jwtService.extractUserName(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

                boolean isValidToken = tokenRepository.findByToken(token)
                        .map(t -> !t.isRevoked() && !t.isExpired())
                        .orElse(false);

                if (jwtService.validateToken(token, userDetails) && isValidToken) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());

            if (token != null) {
                Optional<Token> tokenOptional = tokenRepository.findByToken(token);
                tokenOptional.ifPresent(t -> {
                    if (!t.isExpired() || !t.isRevoked()) {
                        t.setExpired(true);
                        t.setRevoked(true);
                        tokenRepository.save(t);
                        System.out.println("Token expired and revoked. Updated in DB.");
                    }
                });
            }
        }

        filterChain.doFilter(request, response);
    }
}

