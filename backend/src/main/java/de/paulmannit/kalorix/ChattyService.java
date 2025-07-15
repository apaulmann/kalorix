package de.paulmannit.kalorix;

import de.paulmannit.kalorix.client.OpenAIClient;
import de.paulmannit.kalorix.dto.CompletionRequest;
import de.paulmannit.kalorix.dto.CompletionResponse;
import de.paulmannit.kalorix.dto.Message;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class ChattyService {
    @Inject
    @RestClient
    OpenAIClient openAIClient;

    @ConfigProperty(name = "openai.api.key")
    String apiKey;

    public int getCalories(String food) {
        CompletionRequest request = new CompletionRequest();
        request.messages = List.of(
                new Message("system", "Antworte ausschließlich mit der Kalorienanzahl eines Lebensmittels. Gib nur eine Zahl ohne Text oder Einheit zurück."),
                new Message("user", "Wie viele Kalorien hat das Lebensmittel " + food + "?")
        );
        CompletionResponse response = openAIClient.chat("Bearer " + apiKey, request);
        Log.info(response.choices.getFirst().message.getContent());

        return Integer.parseInt(response.choices.getFirst().message.getContent());
    }
}
