package com.windfit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Tai khoan khong duoc de trong")
    private String username;
    @NotBlank(message = "Mat khau khong duoc de trong")
    private String password;
}
