package de.paulmannit.kalorix.api;

import de.paulmannit.kalorix.dto.UserKalTrackingDto;
import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.model.UserKalTrackingEty;
import de.paulmannit.kalorix.repository.UserKalTrackingRepository;
import de.paulmannit.kalorix.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("/api/kaltracking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserKalTrackingResource {

    @Inject
    UserKalTrackingRepository repository;

    @Inject
    UserRepository userRepository; // Assuming you have a UserRepository to fetch UserEty

    @GET
    @Path("/all/{user}")
    public List<UserKalTrackingDto> getAllForUserAll(@PathParam("user") String userName) {
        return repository.findByUserName(userName)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @GET
    @Path("/today/{user}")
    public List<UserKalTrackingDto> getAllForUserToday(@PathParam("user") String userName) {
        return repository.findByUserNameAndToday(userName)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @POST
    @Path("/{user}")
    @Transactional
    public Response createEntry(@PathParam("user") String userName, UserKalTrackingDto dto) {
        Optional<UserEty> user = userRepository.findByUsername(userName);
        if (user.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User nicht gefunden").build();
        }

        UserKalTrackingEty entity = mapFromDto(dto, user.get());
        entity.persist();
        return Response.status(Response.Status.CREATED).entity(mapToDto(entity)).build();
    }

    @PUT
    @Path("/{user}")
    @Transactional
    public Response updateEntry(@PathParam("user") String userName, UserKalTrackingDto dto) {
        Optional<UserKalTrackingEty> existing = Optional.ofNullable(repository.findById(dto.getId()));
        if (existing.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        UserKalTrackingEty entity = existing.get();
        entity.kcal = dto.getKcal();
        entity.beschreibung = dto.getBeschreibung();
        entity.zeitstempel = dto.getZeitstempel();
        return Response.ok(mapToDto(entity)).build();
    }

    @DELETE
    @Path("/{user}")
    @Transactional
    public Response deleteEntry(@PathParam("user") String userName, UserKalTrackingDto dto) {
        Optional<UserEty> user = userRepository.findByUsername(userName);
        if (user.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User nicht gefunden").build();
        }

        UserKalTrackingEty entity = repository.findById(dto.getId());
        entity.delete();
        return Response.status(Response.Status.OK).build();
    }

    private UserKalTrackingDto mapToDto(UserKalTrackingEty entity) {
        UserKalTrackingDto dto = new UserKalTrackingDto();
        dto.setId(entity.id);
        dto.setZeitstempel(entity.zeitstempel);
        dto.setKcal(entity.kcal);
        dto.setBeschreibung(entity.beschreibung);
        return dto;
    }

    private UserKalTrackingEty mapFromDto(UserKalTrackingDto dto, UserEty user) {
        UserKalTrackingEty entity = new UserKalTrackingEty();
        entity.user = user;
        entity.zeitstempel = dto.getZeitstempel() != null ? dto.getZeitstempel() : LocalDateTime.now();
        entity.kcal = dto.getKcal();
        entity.beschreibung = dto.getBeschreibung();
        return entity;
    }
}
