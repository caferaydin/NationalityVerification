package com.nationalityverification.application.service;

import com.nationalityverification.api.dto.request.WebhookRequest;
import com.nationalityverification.common.logging.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookService {

    public void process(WebhookRequest request) {
        log.info("Analysis received | tcknHmac={} | status={} | score={}",
                LoggingUtils.tcknHmac(request.getTckn()),
                request.getAnalyzedData().isVerificationStatus(),
                request.getAnalyzedData().getVerificationScore());
    }
}
