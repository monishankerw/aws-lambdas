package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.model.ChatMessage;
import org.example.repository.ChatMessageRepository;
import org.example.service.ChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository repo;

    public ChatMessageServiceImpl(ChatMessageRepository repo) {
        this.repo = repo;
    }

    /**
     * Asynchronous creation of a message using Spring's @Async
     * This method will run on a separate thread.
     */
    @Async
    @Override
    public CompletableFuture<ChatMessage> createAsync(ChatMessage m) {
        log.info("[THREAD] createAsync() executed on thread: {}", Thread.currentThread().getName());
        log.debug("[SERVICE] Payload: sender='{}', text(len)={}", m.getSender(), m.getText() != null ? m.getText().length() : 0);

        m.setId(null);
        if (m.getCreatedAt() == null) m.setCreatedAt(Instant.now());

        ChatMessage saved = repo.save(m);
        log.info("[SERVICE] createAsync() - success id={}", saved.getId());
        return CompletableFuture.completedFuture(saved);
    }

    /**
     * Normal synchronous method (still logged)
     */
    @Override
    public ChatMessage create(ChatMessage m) {
        log.info("[SERVICE] create() - start (sync)");
        log.debug("[SERVICE] Payload: sender='{}', text(len)={}", m.getSender(), m.getText() != null ? m.getText().length() : 0);

        m.setId(null);
        if (m.getCreatedAt() == null) m.setCreatedAt(Instant.now());

        ChatMessage saved = repo.save(m);
        log.info("[SERVICE] create() - success id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<ChatMessage> get(String id) {
        log.info("[SERVICE] get() - id={}", id);
        Optional<ChatMessage> result = repo.findById(id);
        log.debug("[SERVICE] get() - found? {}", result.isPresent());
        return result;
    }

    @Override
    public Page<ChatMessage> list(String sender, Pageable pageable) {
        log.info("[SERVICE] list() - sender='{}', page={}, size={}",
                sender, pageable.getPageNumber(), pageable.getPageSize());
        Page<ChatMessage> page = (sender != null && !sender.trim().isEmpty())
                ? repo.findBySenderIgnoreCase(sender.trim(), pageable)
                : repo.findAll(pageable);
        log.debug("[SERVICE] list() - totalElements={}, totalPages={}",
                page.getTotalElements(), page.getTotalPages());
        return page;
    }

    @Async // Asynchronous update
    @Override
    public CompletableFuture<ChatMessage> updateAsync(String id, ChatMessage m) {
        log.info("[THREAD] updateAsync() running on thread: {}", Thread.currentThread().getName());

        ChatMessage existing = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("[SERVICE] updateAsync() - not found id={}", id);
                    return new NoSuchElementException("Message not found: " + id);
                });

        if (m.getSender() != null && !m.getSender().trim().isEmpty()) {
            log.debug("[SERVICE] updateAsync() - sender: '{}' -> '{}'", existing.getSender(), m.getSender().trim());
            existing.setSender(m.getSender().trim());
        }
        if (m.getText() != null && !m.getText().trim().isEmpty()) {
            log.debug("[SERVICE] updateAsync() - text len: {} -> {}",
                    existing.getText() != null ? existing.getText().length() : 0,
                    m.getText().trim().length());
            existing.setText(m.getText().trim());
        }

        ChatMessage saved = repo.save(existing);
        log.info("[SERVICE] updateAsync() - success id={}", saved.getId());
        return CompletableFuture.completedFuture(saved);
    }

    @Override
    public ChatMessage update(String id, ChatMessage m) {
        log.info("[SERVICE] update() - id={}", id);
        ChatMessage existing = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("[SERVICE] update() - not found id={}", id);
                    return new NoSuchElementException("Message not found: " + id);
                });

        if (m.getSender() != null && !m.getSender().trim().isEmpty()) {
            log.debug("[SERVICE] update() - sender: '{}' -> '{}'", existing.getSender(), m.getSender().trim());
            existing.setSender(m.getSender().trim());
        }
        if (m.getText() != null && !m.getText().trim().isEmpty()) {
            log.debug("[SERVICE] update() - text(len): {} -> {}",
                    existing.getText() != null ? existing.getText().length() : 0,
                    m.getText().trim().length());
            existing.setText(m.getText().trim());
        }

        ChatMessage saved = repo.save(existing);
        log.info("[SERVICE] update() - success id={}", saved.getId());
        return saved;
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteAsync(String id) {
        log.info("[THREAD] deleteAsync() running on thread: {}", Thread.currentThread().getName());
        repo.deleteById(id);
        log.info("[SERVICE] deleteAsync() - success id={}", id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void delete(String id) {
        log.info("[SERVICE] delete() - id={}", id);
        repo.deleteById(id);
        log.info("[SERVICE] delete() - success id={}", id);
    }
}
