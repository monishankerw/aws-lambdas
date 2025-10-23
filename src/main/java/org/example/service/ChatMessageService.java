package org.example.service;



import org.example.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public interface ChatMessageService {
    public CompletableFuture<ChatMessage> createAsync(ChatMessage m);
    public ChatMessage create(ChatMessage m);
    Optional<ChatMessage> get(String id);
    public CompletableFuture<ChatMessage> updateAsync(String id, ChatMessage m);
    Page<ChatMessage> list(String sender, Pageable pageable);
    public CompletableFuture<Void> deleteAsync(String id);
    ChatMessage update(String id, ChatMessage m);

    void delete(String id);
}