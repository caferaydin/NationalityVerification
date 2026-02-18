package com.nationalityverification.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

/**
 * Nested DTO inside {@link WebhookRequest} carrying the Upsonic analysis result.
 */
public class AnalyzedDataDto {

    @JsonProperty("verification_status")
    private boolean verificationStatus;

    @JsonProperty("verification_score")
    @DecimalMin(value = "0.0", message = "verification_score must be >= 0")
    @DecimalMax(value = "1.0", message = "verification_score must be <= 1")
    private double verificationScore;

    @JsonProperty("verification_description")
    @Size(max = 1024, message = "verification_description must not exceed 1024 characters")
    private String verificationDescription;

    public boolean isVerificationStatus()            { return verificationStatus; }
    public void    setVerificationStatus(boolean s)  { this.verificationStatus = s; }
    public double  getVerificationScore()            { return verificationScore; }
    public void    setVerificationScore(double s)    { this.verificationScore = s; }
    public String  getVerificationDescription()      { return verificationDescription; }
    public void    setVerificationDescription(String d) { this.verificationDescription = d; }
}
