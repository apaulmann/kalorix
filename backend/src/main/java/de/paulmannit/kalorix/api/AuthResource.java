package de.paulmannit.kalorix.api;

import de.paulmannit.kalorix.dto.LoginRequest;
import de.paulmannit.kalorix.dto.LoginResponse;
import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.service.JwtService;
import de.paulmannit.kalorix.service.UserService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    @Inject
    JwtService jwtService;

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        if (!userService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Ungültiger Benutzername oder Passwort"))
                    .build();
        }
        UserEty userEty = userService.getUserByName(loginRequest.getUsername());
        String token = jwtService.generateToken(userEty.username, userEty.role);
        Log.info(token);
        return Response.ok(new LoginResponse(token)).build();
    }

    // Einfache Hilfsklasse für Fehlermeldungen
    @RegisterForReflection
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    @POST
    @Path("/refresh")
    public Response refresh() {
        // Simplified refresh logic - in production, you'd validate the refresh token
        return Response.ok("{\"message\": \"Token refresh not implemented\"}")
                .build();
    }
}