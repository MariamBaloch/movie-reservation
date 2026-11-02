package com.moviereservation.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank (message = "Email address is required")
    @Email(message = "Email address must be valid")
    private String email;

    @NotBlank (message = "Password is required")
    @Size(min=8, message = "Password must be at least 8 characters")
    private String password;
}
