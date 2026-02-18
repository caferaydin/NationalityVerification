package com.nationalityverification.infrastructure.persistence;

import com.nationalityverification.domain.model.IdentityVerificationResult;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link IdentityVerificationResultRepository}.
 * Keyed by TCKN; only the latest result per TCKN is retained.
 */
@Repository
public class InMemoryVerificationResultRepository implements IdentityVerificationResultRepository {

    private final Map<String, IdentityVerificationResult> store = new ConcurrentHashMap<>();

    @Override
    public IdentityVerificationResult save(IdentityVerificationResult result) {
        store.put(result.getTckn(), result);
        return result;
    }

    @Override
    public Optional<IdentityVerificationResult> findLatestByTckn(String tckn) {
        return Optional.ofNullable(store.get(tckn));
    }
}
