package com.alten.ecommerce_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class ServiceJWT {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}") // 24 heures par défaut
    private Long expiration;

    private SecretKey obtenirCle() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String genererToken(String email, boolean estAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", estAdmin);
        return creerToken(claims, email);
    }

    private String creerToken(Map<String, Object> claims, String sujet) {
        return Jwts.builder()
                .claims(claims)
                .subject(sujet)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(obtenirCle(), Jwts.SIG.HS256)
                .compact();
    }

    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public Boolean extraireEstAdmin(String token) {
        Claims claims = extraireTousClaims(token);
        return claims.get("admin", Boolean.class);
    }

    public Date extraireDateExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }

    public <T> T extraireClaim(String token, Function<Claims, T> resolveurClaims) {
        final Claims claims = extraireTousClaims(token);
        return resolveurClaims.apply(claims);
    }

    private Claims extraireTousClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenirCle())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean estTokenExpire(String token) {
        return extraireDateExpiration(token).before(new Date());
    }

    public Boolean validerToken(String token, String email) {
        final String emailToken = extraireEmail(token);
        return (emailToken.equals(email) && !estTokenExpire(token));
    }
}
