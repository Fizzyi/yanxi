package com.yanxi.yanxiapi.utils;

import com.yanxi.yanxiapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    private static final String SECRET_KEY = "yanxi_secret_key"; // 生产环境应使用更安全的密钥
    private static final long EXPIRATION_TIME = 1000 * 60 * 30; // 30分钟
    private static final long REFRESH_THRESHOLD = 1000 * 60 * 5; // 5分钟阈值

    // 生成JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("userType", user.getUserType())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 验证JWT
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 提取用户ID
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    // 提取用户类型
    public String extractUserType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userType", String.class);
    }

    // 提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 检查token是否需要刷新（剩余时间少于5分钟）
    public Boolean shouldRefreshToken(String token) {
        if (isTokenExpired(token)) {
            return false; // 已过期，不需要刷新，需要重新登录
        }
        
        Date expiration = extractExpiration(token);
        long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
        return timeUntilExpiration <= REFRESH_THRESHOLD;
    }

    // 获取token剩余时间（毫秒）
    public Long getTimeUntilExpiration(String token) {
        if (isTokenExpired(token)) {
            return 0L;
        }
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    // 刷新token（为现有用户生成新token）
    public String refreshToken(String token) {
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
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}