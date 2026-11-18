package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Properties;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JwtUtil {
    private static final String JWT_SECRET;
    private static final long JWT_EXPIRATION_MS;
    private static final Key SECRET_KEY;

    static {
        Properties env = new Properties();
        
        // 1. First priority: System environment variables (Render, production)
        String jwtSecret = System.getenv("JWT_SECRET");
        String jwtExp = System.getenv("JWT_EXPIRATION_MS");
        
        if (jwtSecret != null) env.setProperty("JWT_SECRET", jwtSecret);
        if (jwtExp != null) env.setProperty("JWT_EXPIRATION_MS", jwtExp);
        
        // 2. Second priority: .env file (local development)
        try (InputStream input = Files.newInputStream(java.nio.file.Path.of(".env"))) {
            Properties fileProps = new Properties();
            fileProps.load(input);
            // Only set if not already set by system env
            if (jwtSecret == null) env.setProperty("JWT_SECRET", fileProps.getProperty("JWT_SECRET", "default-secret-key-change-me-in-production"));
            if (jwtExp == null) env.setProperty("JWT_EXPIRATION_MS", fileProps.getProperty("JWT_EXPIRATION_MS", "86400000"));
        } catch (Exception e) {
            System.err.println("[JwtUtil] File .env tidak ditemukan, menggunakan system env: " + e.getMessage());
        }
        
        String secret = env.getProperty("JWT_SECRET", "defaultSecretKey");
        JWT_SECRET = secret;
        JWT_EXPIRATION_MS = Long.parseLong(env.getProperty("JWT_EXPIRATION_MS", "86400000"));
        SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public static String generateToken(int userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("[JwtUtil] Token tidak valid: " + e.getMessage());
            return null;
        }
    }

    public static int getUserId(Claims claims) {
        return Integer.parseInt(claims.getSubject());
    }

    public static String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    public static String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public static boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}