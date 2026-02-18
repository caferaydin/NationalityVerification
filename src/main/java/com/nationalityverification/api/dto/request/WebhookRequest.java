package com.nationalityverification.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Inbound DTO for the Upsonic identity-analysis webhook.
 * {@code tckn} may arrive as either a JSON number or a JSON string.
 */
public class WebhookRequest {

    @JsonDeserialize(using = TcknDeserializer.class)
    @NotNull(message = "tckn is required")
    @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits")
    private String tckn;

    @JsonProperty("analyzed_data")
    @NotNull(message = "analyzed_data is required")
    @Valid
    private AnalyzedDataDto analyzedData;

    // Jackson needs the setter; TCKN arrives as a JSON number so a custom deserialiser is used.
    public String getTckn()                       { return tckn; }
    public void   setTckn(String tckn)            { this.tckn = tckn; }
    public AnalyzedDataDto getAnalyzedData()       { return analyzedData; }
    public void   setAnalyzedData(AnalyzedDataDto d) { this.analyzedData = d; }
}
