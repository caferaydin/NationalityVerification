package com.nationalityverification.application.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nationalityverification.application.port.in.GenerateQrUseCase;
import com.nationalityverification.common.exception.NationalityVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Generates QR code PNG images using ZXing.
 */
@Service
public class QrService implements GenerateQrUseCase {

    private static final Logger log = LoggerFactory.getLogger(QrService.class);

    private static final String QR_BASE_URL = "https://demoqr.upsonic.ai/demo/qr/?sessionId=";
    private static final int    QR_SIZE     = 300;

    @Override
    public byte[] generateQr(String sessionId) {
        String content = QR_BASE_URL + sessionId;
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
