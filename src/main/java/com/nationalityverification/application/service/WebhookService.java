package com.nationalityverification.application.service;

import com.nationalityverification.application.port.in.ProcessWebhookUseCase;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.domain.model.IdentityVerificationResult;
import com.nationalityverification.infrastructure.persistence.IdentityVerificationResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service that persists and processes the identity verification result
 * coming from the Upsonic webhook.
 */
@Service
public class WebhookService implements ProcessWebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final IdentityVerificationResultRepository resultRepository;

    public WebhookService(IdentityVerificationResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public void process(IdentityVerificationResult result) {
        log.info("Processing verification result | tcknHmac={} | status={} | score={}",
                 LoggingUtils.tcknHmac(result.getTckn()),
                 result.getStatus(),
                 result.getScore());

        resultRepository.save(result);

        log.debug("Verification result persisted | tcknHmac={}", LoggingUtils.tcknHmac(result.getTckn()));
    }
}
