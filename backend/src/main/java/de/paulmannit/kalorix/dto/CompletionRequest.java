package de.paulmannit.kalorix.dto;

import java.util.List;

public class CompletionRequest {
    public String model = "gpt-4";
    public List<Message> messages;
}
