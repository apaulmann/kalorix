package de.paulmannit.kalorix.dto;

import de.paulmannit.kalorix.model.BewegungsProfil;
import de.paulmannit.kalorix.model.Geschlecht;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RegisterForReflection
public class UserDetailsDto {
    private Integer groesse;
    private LocalDate dateOfBirth;
    private Geschlecht geschlecht;
    private BewegungsProfil bewegungsProfil;
    private Integer gewicht;
    private Integer kcalVerbrauch;
}