package com.kien.parking_system.security.services;

import com.kien.parking_system.models.ETokenType;
import com.kien.parking_system.models.User;
import com.kien.parking_system.models.UserToken;
import com.kien.parking_system.repositories.UserRepository;
import com.kien.parking_system.repositories.UserTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserTokenService {

    @Value("${parking.app.RefreshTokenExpirationDays}")
    private Long rfTokenDuration;

    private final UserRepository userRepository;

    private final UserTokenRepository userTokenRepository;


    public UserTokenService(UserRepository userRepository, UserTokenRepository userTokenRepository) {
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
    }

    public Optional<UserToken> findByToken(String token){
        return userTokenRepository.findByToken(token);
    }

    public UserToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("Tài khoản không tồn tại."));

        UserToken refreshToken = new UserToken();

        refreshToken.setUser(user);
        refreshToken.setTokenType(ETokenType.REFRESH);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(rfTokenDuration));
        refreshToken.setIsRevoked(false);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = userTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public UserToken verifyExpiration(UserToken token){
        if (token.getExpiresAt().isBefore(LocalDateTime.now())){
            token.setIsRevoked(true);
            userTokenRepository.save(token);
            throw new RuntimeException(("Token đã hết hạn."));
        }
        return token;
    }

}
