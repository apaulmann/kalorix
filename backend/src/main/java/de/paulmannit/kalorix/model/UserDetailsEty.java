package de.paulmannit.kalorix.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "userdetails")
public class UserDetailsEty extends PanacheEntity {
    public Integer groesse;
    public Integer gewicht;
    public LocalDate geburtsdatum;
    @Enumerated(EnumType.STRING)
    public Geschlecht geschlecht;
    @Enumerated(EnumType.STRING)
    public BewegungsProfil bewegungsProfil;
    public Integer kcalVerbrauch;

    @OneToOne(mappedBy = "details")
    public UserEty user;
}