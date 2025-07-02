package com.yanxi.yanxiapi.utils;

import com.yanxi.yanxiapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret:yanxi_secret_key_for_production_use_stronger_key}")
    private String secretKey;
    
    @Value("${app.jwt.expiration:1800000}") // 30分钟 (30 * 60 * 1000)
    private long expirationTime;
    
    @Value("${app.jwt.refresh-threshold:300000}") // 5分钟阈值 (5 * 60 * 1000)
    private long refreshThreshold;

    private SecretKey getSigningKey() {
        // 使用更安全的密钥生成方式
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成JWT - 优化性能
    public String generateToken(User user) {
        try {
            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("userId", user.getId())
                    .claim("userType", user.getUserType())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {}", user.getUsername(), e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证JWT - 添加异常处理
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // 提取用户名 - 添加异常处理
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.debug("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    // 提取用户ID - 添加异常处理
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.debug("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    // 提取用户类型 - 添加异常处理
    public String extractUserType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userType", String.class);
        } catch (Exception e) {
            log.debug("Error extracting user type from token: {}", e.getMessage());
            return null;
        }
    }

    // 提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 检查token是否需要刷新（剩余时间少于阈值）
    public Boolean shouldRefreshToken(String token) {
        try {
            if (isTokenExpired(token)) {
                return false; // 已过期，不需要刷新，需要重新登录
            }
            
            Date expiration = extractExpiration(token);
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiration <= refreshThreshold;
        } catch (Exception e) {
            log.debug("Error checking token refresh status: {}", e.getMessage());
            return false;
        }
    }

    // 获取token剩余时间（毫秒）
    public Long getTimeUntilExpiration(String token) {
        try {
            if (isTokenExpired(token)) {
                return 0L;
            }
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.debug("Error getting time until expiration: {}", e.getMessage());
            return 0L;
        }
    }

    // 刷新token（为现有用户生成新token）
    public String refreshToken(String token) {
        try {
            if (isTokenExpired(token)) {
                return null; // 已过期，无法刷新
            }
            
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String userType = claims.get("userType", String.class);
            
            return Jwts.builder()
                    .setSubject(username)
                    .claim("userId", userId)
                    .claim("userType", userType)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return null;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.debug("Error checking token expiration: {}", e.getMessage());
            return true; // 如果无法解析，认为已过期
        }
    }

    /**
     * 验证token格式是否正确
     */
    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中提取所有必要信息，用于性能优化
     */
    public TokenInfo extractTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return TokenInfo.builder()
                    .username(claims.getSubject())
                    .userId(claims.get("userId", Long.class))
                    .userType(claims.get("userType", String.class))
                    .issuedAt(claims.getIssuedAt())
                    .expiration(claims.getExpiration())
                    .build();
        } catch (Exception e) {
            log.debug("Error extracting token info: {}", e.getMessage());
            return null;
        }
    }

    // Token信息封装类
    @lombok.Data
    @lombok.Builder
    public static class TokenInfo {
        private String username;
        private Long userId;
        private String userType;
        private Date issuedAt;
        private Date expiration;
        
        public boolean isExpired() {
            return expiration.before(new Date());
        }
        
        public boolean shouldRefresh(long refreshThreshold) {
            if (isExpired()) {
                return false;
            }
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiration <= refreshThreshold;
        }
    }
}