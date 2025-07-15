package de.paulmannit.kalorix.api;

import de.paulmannit.kalorix.ChattyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/chatty")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChattyResource {

    @Inject
    ChattyService chattyService;

    @GET
    @RolesAllowed({"amdin", "user"})
    @Path("/{food}")
    public Integer askChatty(@PathParam("food") String food) {
        return chattyService.getCalories(food);
    }
}
