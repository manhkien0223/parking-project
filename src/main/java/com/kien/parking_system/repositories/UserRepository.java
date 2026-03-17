package com.kien.parking_system.repositories;

import com.kien.parking_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {



    @Query(value = "SELECT EXISTS(SELECT * FROM users WHERE email = :email)",
            nativeQuery = true)
    Boolean existsUserByEmail(@Param("email") String email);

    @Query(value = "SELECT EXISTS(SELECT * FROM users WHERE phone_number = :phoneNumber)",
            nativeQuery = true)
    Boolean existByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT * FROM users WHERE email = :email",
            nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE phone_number = :phoneNumber",
            nativeQuery = true)
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
