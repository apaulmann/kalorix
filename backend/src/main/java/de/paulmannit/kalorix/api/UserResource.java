package de.paulmannit.kalorix.api;

import de.paulmannit.kalorix.dto.UserDto;
import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.service.UserService;
import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @RolesAllowed("admin")
    public Response getAllPersons() {
        Log.info("getAllPersons");
        List<UserEty> persons = userService.getAllUsers();
        List<UserDto> personDTOs = persons.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return Response.ok(personDTOs).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response getPersonById(@PathParam("id") Long id) {
        UserEty person = userService.getUserById(id);
        return Response.ok(mapToDTO(person)).build();
    }

    @GET
    @Path("/token/{token}")
    public Response getPersonByToken(@PathParam("token") String token) {
        UserEty person = userService.getUserByAccessToken(token);
        return Response.ok(mapToPublicDTO(person)).build();
    }

    @POST
    @RolesAllowed("admin")
    public Response createPerson(UserDto personDTO) {
        UserEty person = mapFromDTO(personDTO);
        person.role = "user";
        person = userService.createUser(person);
        return Response.created(URI.create("/api/persons/" + person.id))
                .entity(mapToDTO(person))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updatePerson(@PathParam("id") Long id, UserDto personDTO) {
        UserEty updatedPerson = userService.updateUser(id, mapFromDTO(personDTO));
        return Response.ok(mapToDTO(updatedPerson)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deletePerson(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    // Helper methods for DTO mapping
    private UserDto mapToDTO(UserEty user) {
        UserDto dto = new UserDto();
        dto.setId(user.id);
        dto.setName(user.username);
        dto.setEmail(user.email);
        dto.setCreatedAt(user.createdAt);
        dto.setUpdatedAt(user.updatedAt);
        return dto;
    }

    private UserDto mapToPublicDTO(UserEty user) {
        UserDto dto = new UserDto();
        dto.setId(user.id);
        dto.setName(user.username);
        // E-Mail und Token werden nicht für die öffentliche Ansicht freigegeben
        return dto;
    }

    private UserEty mapFromDTO(UserDto dto) {
        UserEty user = new UserEty();
        // ID wird nicht gemappt (wird automatisch generiert)
        user.username = dto.getName();
        user.email = dto.getEmail();
        user.password = dto.getPassword();
        return user;
    }
}