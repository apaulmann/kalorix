package de.paulmannit.kalorix.client;

import de.paulmannit.kalorix.dto.CompletionRequest;
import de.paulmannit.kalorix.dto.CompletionResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@RegisterClientHeaders
public interface OpenAIClient {

    @POST
    @Path("/chat/completions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionResponse chat(@HeaderParam("Authorization") String auth, CompletionRequest request);
}