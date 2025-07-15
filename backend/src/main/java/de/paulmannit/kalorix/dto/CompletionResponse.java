package de.paulmannit.kalorix.dto;

import java.util.List;

public class CompletionResponse {
    public List<Choice> choices;

    public static class Choice {
        public Message message;
    }
}
