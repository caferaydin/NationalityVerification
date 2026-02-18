package com.nationalityverification.application.store;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for the most recent verification result per TCKN.
 * Used to enforce the TTL window before allowing photo uploads.
 */
@Component
public class VerificationStore {

    public record Entry(boolean status, double score, Instant receivedAt) {}

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public void put(String tckn, boolean status, double score) {
        store.put(tckn, new Entry(status, score, Instant.now()));
    }

    public Entry get(String tckn) {
        return store.get(tckn);
    }
}
