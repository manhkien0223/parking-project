package com.kien.parking_system.repositories;

import com.kien.parking_system.models.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    @Query(value = "SELECT * FROM user_tokens WHERE token = :token",
            nativeQuery = true)
    Optional<UserToken> findByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM user_tokens " +
            "WHERE user_id = :userId AND is_revoked = 0",
            nativeQuery = true
    )
    List<UserToken> findByUserIdAndRevokedStatus(@Param("userId") Long userId);
}
