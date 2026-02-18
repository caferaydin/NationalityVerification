package com.nationalityverification.application.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nationalityverification.common.exception.NationalityVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Generates QR code PNG images using ZXing.
 */
@Slf4j
@Service
public class QrService {

    private static final int QR_SIZE = 300;

    private final String qrBaseUrl;

    public QrService(@Value("${qr.base-url:https://demoqr.upsonic.ai/demo/qr/}") String qrBaseUrl) {
        this.qrBaseUrl = qrBaseUrl;
    }

    public byte[] generateQr(String sessionId) {
        String content = qrBaseUrl + sessionId;
        log.debug("Generating QR for sessionId=[{}]", sessionId);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(
                content,
                BarcodeFormat.QR_CODE,
                QR_SIZE, QR_SIZE,
                Map.of(EncodeHintType.MARGIN, 1)
            );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("QR generation failed for sessionId=[{}]", sessionId, e);
            throw new NationalityVerificationException(
                "QR_GENERATION_ERROR",
                "Failed to generate QR code: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
