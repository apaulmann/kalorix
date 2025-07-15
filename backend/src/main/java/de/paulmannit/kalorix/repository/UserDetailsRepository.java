package de.paulmannit.kalorix.repository;

import de.paulmannit.kalorix.model.UserDetailsEty;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserDetailsRepository implements PanacheRepository<UserDetailsEty> {
}