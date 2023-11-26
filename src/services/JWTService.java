package services;

import domain.entites.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JWTService {

    // This is used to encode and decode our JWT tokens when we need to
    public static final String SECRET_KEY = "e2db7f706d5453ae863d0f8aed65b4525f3c265675dd7f5ba244e47c9170aa0f";

    // Using this to decode and extract the user email from the JWT token
    public String extractUserEmail(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject);
    }

    // Extracting single claim
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }


    // Generating a JWT token without any additional claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generating JWT token out of extracting the data we've gathered during authentication
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Checking whether the JWT token is valid
    // We want to validate if this token belongs to this user
    public boolean isValid(String jwtToken, UserDetails userDetails) {
        final String userEmail = extractUserEmail(jwtToken);
        return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(jwtToken);
    }

    // Making sure the token isn't out of date and expired
    public boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    // Extracting the expiration date of the jwt token
    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    // Extracting all Claims
    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(jwtToken).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBites = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBites);
    }

}
