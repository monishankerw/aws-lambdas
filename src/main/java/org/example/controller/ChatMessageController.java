package org.example.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ApiResponse;
import org.example.model.ChatMessage;
import org.example.service.ChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@Validated
public class ChatMessageController {

    private final ChatMessageService service;

    public ChatMessageController(ChatMessageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatMessage>> create(@Valid @RequestBody ChatMessage m) {
        log.info("[CTRL] POST /api/messages - create request received");
        log.debug("[CTRL] Payload: {}", m);
        ChatMessage saved = service.create(m);
        log.info("[CTRL] Created id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> get(@PathVariable String id) {
        log.info("[CTRL] GET /api/messages/{} - fetch request", id);
        return service.get(id)
                .<ResponseEntity<ApiResponse<?>>>map(cm -> {
                    log.info("[CTRL] Found id={}", cm.getId());
                    return ResponseEntity.ok(ApiResponse.success("Fetched", cm));
                })
                .orElseGet(() -> {
                    log.warn("[CTRL] Not found id={}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Not Found"));
                });
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChatMessage>>> list(
            @RequestParam(required = false) String sender,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("[CTRL] GET /api/messages - list request (sender='{}', page={}, size={})", sender, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> data = service.list(sender, pageable);
        log.info("[CTRL] List result -> totalElements={}, totalPages={}", data.getTotalElements(), data.getTotalPages());
        return ResponseEntity.ok(ApiResponse.success("List fetched", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatMessage>> update(@PathVariable String id, @Valid @RequestBody ChatMessage m) {
        log.info("[CTRL] PUT /api/messages/{} - update request", id);
        ChatMessage updated = service.update(id, m);
        log.info("[CTRL] Updated id={}", updated.getId());
        return ResponseEntity.ok(ApiResponse.success("Message updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        log.info("[CTRL] DELETE /api/messages/{} - delete request", id);
        service.delete(id);
        log.info("[CTRL] Deleted id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Message deleted", null));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoSuchElementException e) {
        log.warn("[CTRL] handleNotFound: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
    }
}
