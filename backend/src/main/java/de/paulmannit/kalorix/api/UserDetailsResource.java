package de.paulmannit.kalorix.api;

import de.paulmannit.kalorix.dto.UserDetailsDto;
import de.paulmannit.kalorix.dto.UserDto;
import de.paulmannit.kalorix.model.BewegungsProfil;
import de.paulmannit.kalorix.model.Geschlecht;
import de.paulmannit.kalorix.model.UserDetailsEty;
import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.repository.UserDetailsRepository;
import de.paulmannit.kalorix.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Path("/api/user-details")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserDetailsResource {

    @Inject
    UserDetailsRepository detailsRepo;
    @Inject
    UserRepository userRepo;

    @GET
    @Path("/{user}")
    @RolesAllowed({"admin", "user"})
    public Response getDetails(@PathParam("user") String userName) {
        Optional<UserEty> userEty = userRepo.findByUsername(userName);

        if (userEty.isPresent()) {
            UserDetailsEty detailsEty = userEty.get().details;
            if (detailsEty == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(mapToDTO(detailsEty)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{user}")
    @Transactional
    @RolesAllowed({"admin", "user"})
    public Response setDetails(@PathParam("user") String userName, UserDetailsDto dto) {
        Optional<UserEty> userEty = userRepo.findByUsername(userName);
        if (!userEty.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();

        UserDetailsEty existingDetails = null;
        if (userEty.get().details == null) {
            existingDetails = new UserDetailsEty();
            existingDetails.user = userEty.get();
            existingDetails.geschlecht = dto.getGeschlecht();
            existingDetails.groesse = dto.getGroesse();
            existingDetails.geburtsdatum = dto.getDateOfBirth();
            existingDetails.gewicht = dto.getGewicht();
            existingDetails.bewegungsProfil = dto.getBewegungsProfil();
            existingDetails.kcalVerbrauch = berechneVerbrauch(dto);
            userEty.get().details = existingDetails;
            detailsRepo.persist(existingDetails);
        } else {
            existingDetails = userEty.get().details;
            existingDetails.geschlecht = dto.getGeschlecht();
            existingDetails.groesse = dto.getGroesse();
            existingDetails.geburtsdatum = dto.getDateOfBirth();
            existingDetails.gewicht = dto.getGewicht();
            existingDetails.bewegungsProfil = dto.getBewegungsProfil();
            existingDetails.kcalVerbrauch = berechneVerbrauch(dto);
        }


        return Response.ok(dto).build();
    }

    // Harris-Benedict-Formel
    private int berechneVerbrauch(UserDetailsDto d) {
        if (d.getDateOfBirth() == null || d.getGewicht() == null || d.getGroesse() == null || d.getGeschlecht() == null || d.getBewegungsProfil() == null) {
            return 0; // oder throw new IllegalArgumentException("Missing data")
        }

        int alter = Period.between(d.getDateOfBirth(), LocalDate.now()).getYears();

        double grundumsatz;
        if (d.getGeschlecht() == Geschlecht.m) {
            grundumsatz = 66.47 + (13.7 * d.getGewicht()) + (5.0 * d.getGroesse()) - (6.8 * alter);
        } else {
            grundumsatz = 655.1 + (9.6 * d.getGewicht()) + (1.8 * d.getGroesse()) - (4.7 * alter);
        }

        double faktor = switch (d.getBewegungsProfil()) {
            case VIEL -> 1.7;
            case MITTEL -> 1.4;
            default -> 1.2;
        };

        return (int) Math.round(grundumsatz * faktor);
    }


    // Helper methods for DTO mapping
    private UserDetailsDto mapToDTO(UserDetailsEty userDetailsEty) {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setGeschlecht(userDetailsEty.geschlecht);
        dto.setGroesse(userDetailsEty.groesse);
        dto.setDateOfBirth(userDetailsEty.geburtsdatum);
        dto.setGewicht(userDetailsEty.gewicht);
        dto.setBewegungsProfil(userDetailsEty.bewegungsProfil);
        dto.setKcalVerbrauch(userDetailsEty.kcalVerbrauch);

        return dto;
    }

    private UserDto mapToPublicDTO(UserEty user) {
        UserDto dto = new UserDto();
        dto.setId(user.id);
        dto.setName(user.username);
        // E-Mail und Token werden nicht für die öffentliche Ansicht freigegeben
        return dto;
    }

    private UserEty mapFromDTO(UserDetailsDto dto) {
        UserEty user = new UserEty();
        // ID wird nicht gemappt (wird automatisch generiert)
        return user;
    }
}