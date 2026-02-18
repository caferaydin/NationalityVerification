package com.nationalityverification.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound DTO for the Upsonic identity-analysis webhook.
 * {@code tckn} may arrive as either a JSON number or a JSON string.
 *
 * <p>Jackson requires a no-args constructor + setters for binding;
 * {@code @NoArgsConstructor} and {@code @Setter} satisfy that requirement.
 * {@code @Getter} replaces the hand-written accessors.
 */
@Getter
@Setter
@NoArgsConstructor
public class WebhookRequest {

    @JsonDeserialize(using = TcknDeserializer.class)
    @NotNull(message = "tckn is required")
    @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits")
    private String tckn;

    @JsonProperty("analyzed_data")
    @NotNull(message = "analyzed_data is required")
    @Valid
    private AnalyzedDataDto analyzedData;
}
