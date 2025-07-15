package de.paulmannit.kalorix.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "caltracking")
public class UserKalTrackingEty extends PanacheEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    public UserEty user;

    @Column(nullable = false)
    public LocalDateTime zeitstempel;

    @Column(nullable = false)
    public Integer kcal;

    @Column(length = 255)
    public String beschreibung;

}