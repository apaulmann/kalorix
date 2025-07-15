package de.paulmannit.kalorix.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
@AllArgsConstructor
public class LoginResponse {
    private String token;
}