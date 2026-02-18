package com.nationalityverification.application.port.in;

/**
 * Input port: generates a QR code image for the supplied session identifier.
 */
public interface GenerateQrUseCase {

    /**
     * @param sessionId the session identifier to encode into the QR image
     * @return PNG bytes of the rendered QR code
     */
    byte[] generateQr(String sessionId);
}
