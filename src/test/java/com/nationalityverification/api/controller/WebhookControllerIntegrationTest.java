package com.nationalityverification.api.controller;

import com.nationalityverification.security.WebhookHmacValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the webhook endpoint verifying HMAC security.
 *
 * a) Correct HMAC → 200 OK
 * b) Wrong HMAC   → 401 Unauthorized
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "UPS_WEBHOOK_SECRET=integration-test-secret",
    "webhook.secret=integration-test-secret"
})
class WebhookControllerIntegrationTest {

    private static final String SECRET   = "integration-test-secret";
    private static final String ENDPOINT = "/api/v1/webhooks/identity-analysis";
    private static final String VALID_BODY = """
            {
              "tckn": 11111111111,
              "analyzed_data": {
                "verification_status": true,
                "verification_score": 0.95,
                "verification_description": "All checks passed"
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebhookHmacValidator hmacValidator;

    @Test
    @DisplayName("a) Valid HMAC signature → 200 OK")
    void shouldReturn200ForValidSignature() throws Exception {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        byte[] bodyBytes = VALID_BODY.getBytes(StandardCharsets.UTF_8);
        String signature = hmacValidator.computeSignature(timestamp, bodyBytes);

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signature)
                .header("X-Timestamp", timestamp)
                .content(bodyBytes))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("b) Invalid HMAC signature → 401 Unauthorized")
    void shouldReturn401ForInvalidSignature() throws Exception {
        String timestamp     = String.valueOf(Instant.now().getEpochSecond());
        String wrongSignature = "deadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeef";

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", wrongSignature)
                .header("X-Timestamp", timestamp)
                .content(VALID_BODY))
               .andExpect(status().isUnauthorized());
    }
}
