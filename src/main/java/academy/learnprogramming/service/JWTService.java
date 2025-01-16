package academy.learnprogramming.service;

import academy.learnprogramming.data.model.Token;
import academy.learnprogramming.data.model.UserPrincipal;
import academy.learnprogramming.data.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
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

    private String secretKey = "";

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseCookie generateJwtCookie(String username) {
        String jwt = generateToken(username);
        return ResponseCookie.from("jwtCookie", jwt).path("/").maxAge(2 * 60 * 1000)
                .httpOnly(false)
                .build();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
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


    public String extractUsername(String token) {
        System.out.println("\n\n\n\n\n\n\n " + "Cookie token by username: "  + "\n\n\n\n\n\n\n\n");
        try {
            return extractClaim(token, Claims::getSubject) + "\n\nGaza!!!";
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
        final String username = extractUsername(token);

        if (isTokenExpired(token)) {
            updateTokenStatus(token);
            return false;
        }

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
