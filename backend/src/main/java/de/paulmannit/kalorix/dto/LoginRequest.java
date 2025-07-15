package de.paulmannit.kalorix.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class LoginRequest {
    private String username;

    private String password;
}
