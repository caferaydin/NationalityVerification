package com.nationalityverification.api.controller;

import com.nationalityverification.api.dto.request.WebhookRequest;
import com.nationalityverification.application.service.WebhookService;
import com.nationalityverification.common.logging.LoggingUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/api/v1/kimlik_kart_analizi")
    public ResponseEntity<Void> analyse(@RequestBody @Valid WebhookRequest request) {
        log.info("Analysis received | tcknHmac={}", LoggingUtils.tcknHmac(request.getTckn()));
        webhookService.process(request);
        return ResponseEntity.ok().build();
    }
}
