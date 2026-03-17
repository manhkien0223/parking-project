package com.kien.parking_system.security.jwt;

import com.kien.parking_system.models.User;
import com.kien.parking_system.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${parking.app.SecretKey}")
    private String secretKey;

    @Value("${parking.app.AccessTokenExpirationSeconds}")
    private int accessTokenExpirationSeconds;

    @Value("${parking.app.RefreshTokenExpirationDays}")
    private int refreshTokenExpirationDays;

    @Value("${parking.app.AccessCookieName}") // Đã sửa lỗi dư 1 chữ 's'
    private String accessCookieName;

    @Value("${parking.app.RefreshCookieName}")
    private String refreshCookieName;


    //  Tạo AccessCookie(container chứa accesstoken) thường dùng sau khi login thành công
    public ResponseCookie generateAccessCookie(UserDetailsImpl userDetails) {
        String jwt = generateAccessToken(userDetails);
        return generateCookie(accessCookieName, jwt, "/api", accessTokenExpirationSeconds);
    }

    //  Tạo AccessCookie(container chứa accesstoken) thường dùng khi cần refresh Token, register
    public ResponseCookie generateAccessCookie(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = generateAccessToken(userDetails);
        return generateCookie(accessCookieName, jwt, "/api", accessTokenExpirationSeconds);
    }

    //  Tạo refreshCookie(container chứa refresh token)
    public ResponseCookie generateRefreshCookie(String refreshToken) {
        int maxAgeInSeconds = refreshTokenExpirationDays * 24 * 60 * 60;
        return generateCookie(refreshCookieName, refreshToken, "/api/auth/refreshtoken", maxAgeInSeconds);
    }

    // Hàm này dành cho AuthTokenFilter dùng để lấy Access Token
    public String getAccessTokenfromCookie(HttpServletRequest request) {
        return getCookiesByTokenName(request, accessCookieName);
    }

    // Hàm này dành cho AuthController dùng khi người dùng muốn xin cấp lại Access Token mới
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return getCookiesByTokenName(request, refreshCookieName);
    }

    public String getUsernameFromAccessToken(String token) {
        // Cú pháp mới của bản 0.12.x
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Cú pháp mới: Trả về SecretKey thay vì Key
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    //  Validate Token
    public boolean validateAccessToken(String authToken) {
        try {
            // Cú pháp mới của bản 0.12.x
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    //    Tạo access Token kèm ID và ROLE
    public String generateAccessToken(UserDetailsImpl userDetails) {
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return Jwts.builder()
                .subject(userDetails.getUsername()) // Bản mới lược bỏ chữ 'set'
                .claim("id", userDetails.getId())
                .claim("role", role)
                .issuedAt(new Date())               // Bản mới lược bỏ chữ 'set'
                .expiration(new Date((new Date()).getTime() + (accessTokenExpirationSeconds * 1000L))) // Bản mới lược bỏ chữ 'set'
                .signWith(key())                    // Bản mới tự động nhận diện thuật toán
                .compact();
    }

    private ResponseCookie generateCookie(String name, String value, String path, int maxAge) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(false) // Nhớ đổi thành true khi deploy lên host có HTTPS
                .build();
    }

    private String getCookiesByTokenName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);

        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}