package com.nationalityverification.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nationalityverification.api.dto.request.WebhookRequest;
import com.nationalityverification.application.port.in.ProcessWebhookUseCase;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.domain.model.IdentityVerificationResult;
import com.nationalityverification.security.CachedBodyRequestWrapper;
import com.nationalityverification.security.WebhookHmacValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Thin controller for the Upsonic identity-analysis webhook.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Cache the raw request bytes so that both HMAC validation and JSON binding work.</li>
 *   <li>Delegate HMAC validation to {@link WebhookHmacValidator}.</li>
 *   <li>Map the DTO to a domain object and hand off to the use case.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private static final String HEADER_SIGNATURE = "X-Signature";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";

    private final WebhookHmacValidator   hmacValidator;
    private final ProcessWebhookUseCase  processWebhookUseCase;
    private final ObjectMapper           objectMapper;

    public WebhookController(WebhookHmacValidator hmacValidator,
                             ProcessWebhookUseCase processWebhookUseCase,
                             ObjectMapper objectMapper) {
        this.hmacValidator        = hmacValidator;
        this.processWebhookUseCase = processWebhookUseCase;
        this.objectMapper         = objectMapper;
    }

    /**
     * POST /api/v1/webhooks/identity-analysis
     */
    @PostMapping("/identity-analysis")
    public ResponseEntity<Void> receiveIdentityAnalysis(HttpServletRequest request) throws IOException {

        // Read and cache raw body to allow double-read (HMAC + binding)
        CachedBodyRequestWrapper cachedRequest = new CachedBodyRequestWrapper(request);
        byte[] rawBody = cachedRequest.getCachedBody();

        // HMAC validation – throws WebhookSignatureException on failure (handled by GlobalExceptionHandler)
        hmacValidator.validate(
            request.getHeader(HEADER_SIGNATURE),
            request.getHeader(HEADER_TIMESTAMP),
            rawBody
        );

        // Bind & validate JSON
        WebhookRequest webhookRequest = objectMapper.readValue(rawBody, WebhookRequest.class);
        validateWebhookRequestManually(webhookRequest);

        log.info("Webhook received | tcknHmac={}", LoggingUtils.tcknHmac(webhookRequest.getTckn()));

        // Map to domain object and hand off to use case
        IdentityVerificationResult result = new IdentityVerificationResult(
            webhookRequest.getTckn(),
            webhookRequest.getAnalyzedData().isVerificationStatus(),
            webhookRequest.getAnalyzedData().getVerificationScore(),
            webhookRequest.getAnalyzedData().getVerificationDescription()
        );

        processWebhookUseCase.process(result);

        return ResponseEntity.ok().build();
    }

    private void validateWebhookRequestManually(WebhookRequest req) {
        if (req.getTckn() == null || !req.getTckn().matches("\\d{11}")) {
            throw new jakarta.validation.ConstraintViolationException(
                "tckn must be exactly 11 digits", java.util.Set.of());
        }
        double score = req.getAnalyzedData().getVerificationScore();
        if (score < 0.0 || score > 1.0) {
            throw new jakarta.validation.ConstraintViolationException(
                "verification_score must be between 0 and 1", java.util.Set.of());
        }
    }
}
