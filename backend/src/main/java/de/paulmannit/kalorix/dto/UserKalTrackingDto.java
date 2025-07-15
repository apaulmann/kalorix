package de.paulmannit.kalorix.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RegisterForReflection
public class UserKalTrackingDto {
    private Long id;
    private LocalDateTime zeitstempel;
    private Integer kcal;
    private String beschreibung;
}
