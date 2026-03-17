package com.kien.parking_system.controller;

import com.kien.parking_system.models.ERole;
import com.kien.parking_system.models.User;
import com.kien.parking_system.payloads.request.SignUpRequest;
import com.kien.parking_system.payloads.response.ResponseUtils;
import com.kien.parking_system.repositories.UserRepository;
import com.kien.parking_system.security.jwt.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Register a new account.", description = "Create a new user with the provided credentials and assigns default or specific roles.")
    @SecurityRequirements()
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Register successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request - Username or password is already existed."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Role is not found or database connection error. ")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup( @RequestBody @Valid SignUpRequest request) throws Exception {

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsUserByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new ResponseUtils<>(409, "EXISTED_EMAIL", "Đăng ký thất bại." ));
        }
        if (userRepository.existByPhoneNumber(request.getPhoneNumber())){
            return ResponseEntity.badRequest().body(new ResponseUtils<>(409, "EXITED_PHONE_NUMBER", "Đăng ký thất bại." ));
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullName(request.getFullName());
        user.setPasswordHash(encodedPassword);
        user.setRole(ERole.ROLE_USER);
        user.setIsActive(true);

        userRepository.save(user);
        return ResponseEntity.ok(new ResponseUtils<>(201, "REGISTER_SUCCESS","Đăng ký thành công!"));
    }
    @RequestMapping("signin")
    public ResponseEntity<?> signin(@RequestBody @Valid SignUpRequest request){

    }
}
