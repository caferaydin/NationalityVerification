package com.nationalityverification.domain.model;

import com.nationalityverification.domain.enums.VerificationStatus;

import java.time.Instant;

/**
 * Domain object representing the result of an identity analysis
 * received from the Upsonic webhook.
 */
public class IdentityVerificationResult {

    private final String tckn;
    private final VerificationStatus status;
    private final double score;
    private final String description;
    private final Instant receivedAt;

    public IdentityVerificationResult(String tckn,
                                      boolean verified,
                                      double score,
                                      String description) {
        this.tckn = tckn;
        this.status = verified ? VerificationStatus.VERIFIED : VerificationStatus.REJECTED;
        this.score = score;
        this.description = description;
        this.receivedAt = Instant.now();
    }

    public String getTckn()               { return tckn; }
    public VerificationStatus getStatus() { return status; }
    public double getScore()              { return score; }
    public String getDescription()        { return description; }
    public Instant getReceivedAt()        { return receivedAt; }
}
