package de.paulmannit.kalorix.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(String user, String role) {
        return Jwt.issuer(issuer)
                .upn(user)
                .groups(new HashSet<>(List.of(role)))
                .expiresAt(System.currentTimeMillis() + 3600000) // 1 hour
                .sign();
    }

    public String generateRefreshToken(String user) {
        return Jwt.issuer(issuer)
                .upn(user)
                .groups(Set.of("REFRESH"))
                .expiresAt(System.currentTimeMillis() + 7 * 24 * 3600000) // 7 days
                .sign();
    }
}
