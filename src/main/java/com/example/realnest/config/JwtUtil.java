package com.example.realnest.config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.expiration}")
    private long expirationSeconds;
    private final SecretKey key;
    public JwtUtil(@Value("${jwt.secret}") String base64Secret){
        byte[] keyBytes=Decoders.BASE64.decode(base64Secret);
        this.key=Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(String username){
        long now=System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expireAt=new Date(now+expirationSeconds*1000L);

        // return Jwts.builder().subject(username).issuedAt(issuedAt).expiration(expireAt).signWith(key).compact();
        return Jwts.builder().setSubject(username).setIssuedAt(issuedAt).setExpiration(expireAt).signWith(key).compact();

    }

    public boolean validateToken(String token){
        try{
            // Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
    public String getUsernameFromToken(String token){
        // Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
