package filter;

import util.JwtUtil;
import io.jsonwebtoken.Claims;

public class AuthFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    public static Claims extractClaims(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        return JwtUtil.validateToken(token);
    }

    public static int getUserId(String authHeader) {
        Claims claims = extractClaims(authHeader);
        if (claims == null) return -1;
        return JwtUtil.getUserId(claims);
    }

    public static String getEmail(String authHeader) {
        Claims claims = extractClaims(authHeader);
        if (claims == null) return null;
        return JwtUtil.getEmail(claims);
    }

    public static String getRole(String authHeader) {
        Claims claims = extractClaims(authHeader);
        if (claims == null) return null;
        return JwtUtil.getRole(claims);
    }

    public static boolean isAuthorized(String authHeader, String... allowedRoles) {
        String role = getRole(authHeader);
        if (role == null) return false;
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }
}