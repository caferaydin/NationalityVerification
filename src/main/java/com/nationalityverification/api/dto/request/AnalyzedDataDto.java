package com.nationalityverification.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Nested DTO inside {@link WebhookRequest} carrying the Upsonic analysis result.
 *
 * <p>Jackson requires a no-args constructor + setters for binding.
 * Lombok's {@code @Getter}/{@code @Setter}/{@code @NoArgsConstructor} provide all three.
 */
@Getter
@Setter
@NoArgsConstructor
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
}
