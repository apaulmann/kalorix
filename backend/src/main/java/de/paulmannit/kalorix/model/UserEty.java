package de.paulmannit.kalorix.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
@UserDefinition
public class UserEty extends PanacheEntity {

    @Username
    @Column(unique = true, nullable = false)
    public String username;

    @Column(unique = true)
    public String email;

    @Password
    @Column(nullable = false)
    public String password;

    @Roles
    @Column(nullable = false)
    public String role;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "details_id")
    public UserDetailsEty details;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<UserKalTrackingEty> kalorienEintraege = new ArrayList<>();

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static void add(String username, String password, String email, String role) {
        UserEty userEty = new UserEty();
        userEty.username = username;
        userEty.password = BcryptUtil.bcryptHash(password);
        userEty.email = email;
        userEty.role = role;

        userEty.persist();
    }

    public void updatePassword(String newPassword) {
        this.password = BcryptUtil.bcryptHash(newPassword);
    }

    // Helper method to check if admin exists
    public static boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    public String generateAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}