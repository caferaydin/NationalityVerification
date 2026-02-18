package com.nationalityverification.security;

import com.nationalityverification.common.exception.WebhookSignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link WebhookHmacValidator}.
 *
 * a) Correct HMAC signature → validation passes (no exception)
 * b) Wrong  HMAC signature  → {@link WebhookSignatureException} is thrown (401)
 */
class WebhookHmacValidatorTest {

    private static final String SECRET   = "test-secret-key";
    private static final String RAW_BODY = "{\"tckn\":11111111111,\"analyzed_data\":{\"verification_status\":true,\"verification_score\":0.95,\"verification_description\":\"ok\"}}";

    private WebhookHmacValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WebhookHmacValidator(SECRET);
    }

    @Test
    @DisplayName("a) Valid HMAC signature with current timestamp → passes without exception")
    void shouldPassWithValidSignature() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        byte[] body      = RAW_BODY.getBytes(StandardCharsets.UTF_8);
        String signature = validator.computeSignature(timestamp, body);

        assertThatNoException()
            .isThrownBy(() -> validator.validate(signature, timestamp, body));
    }

    @Test
    @DisplayName("b) Invalid HMAC signature → WebhookSignatureException (UNAUTHORIZED)")
    void shouldFailWithInvalidSignature() {
        String timestamp     = String.valueOf(Instant.now().getEpochSecond());
        byte[] body          = RAW_BODY.getBytes(StandardCharsets.UTF_8);
        String wrongSignature = "0000000000000000000000000000000000000000000000000000000000000000";

        assertThatThrownBy(() -> validator.validate(wrongSignature, timestamp, body))
            .isInstanceOf(WebhookSignatureException.class)
            .hasMessageContaining("Signature mismatch");
    }

    @Test
    @DisplayName("c) Stale timestamp (> 5 min) → WebhookSignatureException")
    void shouldFailWithStaleTimestamp() {
        long staleEpoch   = Instant.now().getEpochSecond() - (WebhookHmacValidator.MAX_SKEW_SECONDS + 10);
        String timestamp  = String.valueOf(staleEpoch);
        byte[] body       = RAW_BODY.getBytes(StandardCharsets.UTF_8);
        String signature  = validator.computeSignature(timestamp, body);

        assertThatThrownBy(() -> validator.validate(signature, timestamp, body))
            .isInstanceOf(WebhookSignatureException.class)
            .hasMessageContaining("skew");
    }

    @Test
    @DisplayName("d) Missing X-Signature header → WebhookSignatureException")
    void shouldFailWithMissingSignatureHeader() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        byte[] body      = RAW_BODY.getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> validator.validate(null, timestamp, body))
            .isInstanceOf(WebhookSignatureException.class)
            .hasMessageContaining("Missing X-Signature");
    }
}
