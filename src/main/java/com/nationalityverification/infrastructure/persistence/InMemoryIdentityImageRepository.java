package com.nationalityverification.infrastructure.persistence;

import com.nationalityverification.domain.model.IdentityImage;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link IdentityImageRepository}.
 * Thread-safe for concurrent requests.
 *
 * <p>Replace this bean with a JPA-backed implementation when persistence is required.
 */
@Repository
public class InMemoryIdentityImageRepository implements IdentityImageRepository {

    private final Map<String, IdentityImage> store = new ConcurrentHashMap<>();

    @Override
    public IdentityImage save(IdentityImage image) {
        store.put(image.getImageId(), image);
        return image;
    }

    @Override
    public Optional<IdentityImage> findById(String imageId) {
        return Optional.ofNullable(store.get(imageId));
    }

    @Override
    public List<IdentityImage> findByTckn(String tckn) {
        return store.values().stream()
                .filter(img -> img.getTckn().equals(tckn))
                .collect(Collectors.toList());
    }
}
