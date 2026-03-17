package com.kien.parking_system.payloads.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    private String email;

    private String password;


}
