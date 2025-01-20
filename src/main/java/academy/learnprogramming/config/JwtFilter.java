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
        String requestPath = request.getRequestURI();
        System.out.println("\n\n\n\n\n\n\n " + "Request is made from: " + request.getRequestURI()+ "\n\n\n\n\n\n\n\n");



        if (requestPath.startsWith("/register") || requestPath.startsWith("/login")) {
            filterChain.doFilter(request, response);
            System.out.println("\n\n\n\nRequest is made to a none (/login or /register) protected route\n\n\n\n");
            return;
        }
        System.out.println("\n\n\n\n\n\n\n " + "doFilterInternal" + "\n\n\n\n\n\n\n\n");
//        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//        }

        try {
            System.out.println("\n\n\n\n\n\n\n " + "Request is made from: " + request.getRequestURI()+ "\n\n\n\n\n\n\n\n");

            token = parseJwt(request);

            System.out.println("\n\n\n\n\n\n\n " + "The cookie from the protected route: "+ token + "\n\n\n\n\n\n\n\n");


            if (token != null) {
                email = jwtService.extractEmail(token);
                System.out.println("\n\n\n\n\n\n\n " + "email: " + email + "\n\n\n\n\n\n\n\n");

            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("\n\n\n\n\n\n\n " + "SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication() + "\n\n\n\n\n\n\n\n");
                UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(email);

                System.out.println("\n\n\n worked \n\n\n");


//                boolean isValidToken = tokenRepository.findByToken(token)
//                        .map(t -> !t.isRevoked() && !t.isExpired())
//                        .orElse(false);

                if (jwtService.validateToken(token, userDetails)) {
                    System.out.println("\n\n\n\n\n\n\n " + "Validate: " + userDetails + "\n\n\n\n\n\n\n\n");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else {
                    System.out.println("\n\n\n\n\n\n\n\n\n " + "validateToken fail: " + "\n\n\n\n\n\n\n\n");
                }
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());

            if (token != null) {
                Optional<Token> tokenOptional = tokenRepository.findByToken(token);
                tokenOptional.ifPresent(t -> {                         System.out.println("Token expired and revoked. Updated in DB.");

//                    if (!t.isExpired() || !t.isRevoked()) {
//                        t.setExpired(true);
//                        t.setRevoked(true);
//                        tokenRepository.save(t);
//                    }
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtService.getJwtFromCookies(request);
    }
}

