package org.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
private String userId;

    @NotBlank
    @Size(max = 20)
    @Indexed
    private String sender;


    @NotBlank
    @Size(max = 500)
    private String text;


    private Instant createdAt = Instant.now();
}