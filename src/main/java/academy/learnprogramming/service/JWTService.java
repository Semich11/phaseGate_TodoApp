package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.model.UserPrincipal;
import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.data.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    @Autowired
    private TokenRepository tokenRepository;

    private final Users user = new Users();

    private final UserPrincipal userPrincipal = new UserPrincipal(user);

    private String secretKey = "";

    private int jwtExpirationMs;

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseCookie generateJwtCookie(String email) {
        String jwt = generateToken(email);
        return ResponseCookie.from("jwtCookie", jwt).path("/").maxAge( jwtExpirationMs / 1000)
                .httpOnly(false)
                .build();
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        jwtExpirationMs = 5 * 60 * 1000;


        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .and()
                .signWith(getKey())
                .compact();

    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "jwtCookie");
        if (cookie != null) {
            System.out.println("COOKIE: " + cookie.getValue());
            return cookie.getValue();
        } else {
            return null;
        }
    }

    private SecretKey getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractEmail(String token) {
        System.out.println("\n\n\n\n\n\n\n " + "Cooki token by email: "  + "\n\n\n\n\n\n\n\n");
        try {
            return extractClaim(token, Claims::getSubject);
        }catch (Exception e) {
            System.out.println("\n\n\n\n"+e.getMessage()+ "Fowl!!! \n\n\n\n\n");
            return "\n\n\n\n An Error occur! \n\n\n\n\n";
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith( getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        System.out.println("\n\nTokin collected by validateToken: " + token + ": " + userDetails + "\n\n");
        final String email = extractEmail(token);
        System.out.println("\n\ntoken extracted in validateToken01: " + token + "\n\n");
        System.out.println("\n\nEmail extracted in validateToken02: " + email + "\n\n");
//
//        if (isTokenExpired(token)) {
//            updateTokenStatus(token);
//            return false;
//        }

//        return (email.equals(userPrincipal.getEmail()) && !isTokenExpired(token));
        return (!isTokenExpired(token));
//        System.out.println("\n\nIs token Expired? "+isTokenExpired(token)+"\n\n");
//        return true;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private void updateTokenStatus(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent()) {
            Token storedToken = tokenOptional.get();
            storedToken.setRevoked(true);
            storedToken.setExpired(true);
            tokenRepository.save(storedToken);
        }
    }

}
