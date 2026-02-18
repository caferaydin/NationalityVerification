package com.nationalityverification.security;

import com.nationalityverification.common.exception.WebhookSignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Validates the HMAC-SHA256 signature on incoming Upsonic webhook requests.
 *
 * <p>Expected headers:
 * <ul>
 *   <li>{@code X-Signature}  – hex(HMAC-SHA256(secret, timestamp + "." + rawBody))</li>
 *   <li>{@code X-Timestamp}  – Unix epoch seconds as a string</li>
 * </ul>
 *
 * <p>Timestamp must be within {@value #MAX_SKEW_SECONDS} seconds of server time.
 */
@Component
public class WebhookHmacValidator {

    static final long MAX_SKEW_SECONDS = 300L; // 5 minutes
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;

    public WebhookHmacValidator(@Value("${webhook.secret}") String secret) {
        this.secret = secret;
    }

    /**
     * Validates the signature and timestamp. Throws {@link WebhookSignatureException} on failure.
     *
     * @param signatureHeader value of {@code X-Signature}
     * @param timestampHeader value of {@code X-Timestamp}
     * @param rawBody         raw request body bytes
     */
    public void validate(String signatureHeader, String timestampHeader, byte[] rawBody) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new WebhookSignatureException("Missing X-Signature header");
        }
        if (timestampHeader == null || timestampHeader.isBlank()) {
            throw new WebhookSignatureException("Missing X-Timestamp header");
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            throw new WebhookSignatureException("Invalid X-Timestamp value: " + timestampHeader);
        }

        long now = Instant.now().getEpochSecond();
        if (Math.abs(now - timestamp) > MAX_SKEW_SECONDS) {
            throw new WebhookSignatureException("Timestamp skew exceeds allowed window");
        }

        String expected = computeSignature(timestampHeader, rawBody);

        if (!constantTimeEquals(expected, signatureHeader.toLowerCase())) {
            throw new WebhookSignatureException("Signature mismatch");
        }
    }

    /**
     * Computes the expected HMAC signature: hex(HMAC-SHA256(secret, timestamp + "." + rawBody))
     */
    public String computeSignature(String timestamp, byte[] rawBody) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            mac.update(timestamp.getBytes(StandardCharsets.UTF_8));
            mac.update((byte) '.');
            mac.update(rawBody);
            return HexFormat.of().formatHex(mac.doFinal());
        } catch (Exception e) {
            throw new WebhookSignatureException("Failed to compute HMAC: " + e.getMessage());
        }
    }

    /** Constant-time string comparison to prevent timing attacks. */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
