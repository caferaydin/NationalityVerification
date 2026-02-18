package com.nationalityverification.common.logging;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Utility class for security-aware logging helpers.
 * <p>
 * TCKN must never appear in raw form in logs. This class provides
 * a deterministic HMAC-SHA256 hash suitable for log correlation
 * without exposing PII.
 */
public final class LoggingUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String LOG_MASK_SECRET = "log-mask-secret";

    private LoggingUtils() {}

    /**
     * Returns a deterministic, non-reversible HMAC-SHA256 hex digest of the given TCKN,
     * keyed with an application-internal constant. Safe to write to logs.
     */
    public static String tcknHmac(String tckn) {
        if (tckn == null) return "null";
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                LOG_MASK_SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] digest = mac.doFinal(tckn.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, 16); // first 16 hex chars
        } catch (Exception e) {
            return "[mask-error]";
        }
    }

    /**
     * Masks a value for logs, showing only the first and last 2 characters.
     */
    public static String mask(String value) {
        if (value == null || value.length() <= 4) return "****";
        return value.charAt(0) + "****" + value.charAt(value.length() - 1);
    }
}
