package com.kien.parking_system.security.services;

import com.kien.parking_system.models.User;
import com.kien.parking_system.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User user;

        if(identifier.contains("@")){
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản có email là: " + identifier));
        }else {
            user = userRepository.findByPhoneNumber(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản có số điện thoại là: " + identifier));

        }

        return null;
    }
}
