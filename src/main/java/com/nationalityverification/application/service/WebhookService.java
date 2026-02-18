package com.nationalityverification.application.service;

import com.nationalityverification.api.dto.request.WebhookRequest;
import com.nationalityverification.application.store.VerificationStore;
import com.nationalityverification.common.logging.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final VerificationStore verificationStore;

    public void process(WebhookRequest request) {
        boolean status = request.getAnalyzedData().isVerificationStatus();
        double  score  = request.getAnalyzedData().getVerificationScore();

        verificationStore.put(request.getTckn(), status, score);

        log.info("Analysis saved | tcknHmac={} | status={} | score={}",
                LoggingUtils.tcknHmac(request.getTckn()), status, score);
    }
}
