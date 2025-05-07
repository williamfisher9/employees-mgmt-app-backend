package com.apps.salaries.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.timeout.ms}")
    private String jwtTimeoutMs;

    private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String generateToken(Authentication authentication) {
        return Jwts.builder().signWith(getKey())
                .subject(((UserDetails) authentication.getPrincipal()).getUsername())
                .expiration(new Date(new Date().getTime() + Integer.parseInt(jwtTimeoutMs)))
                .issuedAt(new Date())
                .compact();
    }

    public void validateToken(String token){
        try{
            Jwts.parser().verifyWith(getKey()).build().parse(token);
        } catch (MalformedJwtException e) {
            LOG.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            LOG.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOG.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e){
            LOG.error("unknown exception occurred: {}", e.getMessage());
        }
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }
}