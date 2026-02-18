package com.nationalityverification.application.port.in;

import com.nationalityverification.domain.model.IdentityVerificationResult;

/**
 * Input port: processes a verified identity-analysis result received via webhook.
 */
public interface ProcessWebhookUseCase {

    /**
     * @param result the domain object constructed from the incoming webhook payload
     */
    void process(IdentityVerificationResult result);
}
