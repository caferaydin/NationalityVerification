package com.nationalityverification.api.dto.response;

public record PhotoUploadResponse(boolean success, String tckn, String imageId, String side) {}
