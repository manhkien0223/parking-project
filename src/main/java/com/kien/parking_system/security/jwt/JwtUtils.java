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

import java.security.Key;
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

    @Value("${parking.app.AccesssCookieName}")
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
        return generateCookie(refreshCookieName, refreshToken, "/api/auth/refreshtoken", refreshTokenExpirationDays);
    }

    // Hàm này dành cho AuthTokenFilter dùng để lấy Access Token
    public String getAccesTokenfromCookies(HttpServletRequest request) {
        return getCookiesByTokenName(request, accessCookieName);
    }

    // Hàm này dành cho AuthController dùng khi người dùng muốn xin cấp lại Access Token mới
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return getCookiesByTokenName(request, refreshCookieName);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    //  Validate Token
    public boolean validateAccessToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
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
                .setSubject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + (accessTokenExpirationSeconds * 1000L)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private ResponseCookie generateCookie(String name, String value, String path, int maxAge) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(false)
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
