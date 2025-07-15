package de.paulmannit.kalorix.repository;

import de.paulmannit.kalorix.model.UserEty;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEty> {

    public Optional<UserEty> findByAccessToken(String accessToken) {
        return find("accessToken", accessToken).firstResultOptional();
    }

    public Optional<UserEty> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }
}