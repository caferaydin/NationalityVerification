package com.nationalityverification.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the three task alias endpoints.
 *
 * a) POST /endpoint/kimlik_kart_analizi        → 200 OK
 * b) POST /endpoint/kimlik_kart_foto_on        → 201 Created
 * c) POST /endpoint/kimlik_kart_foto_arka      → 201 Created
 */
@SpringBootTest
@AutoConfigureMockMvc
class AliasEndpointIntegrationTest {

    private static final String VALID_ANALYSIS_BODY = """
            {
              "tckn": 11111111111,
              "analyzed_data": {
                "verification_status": true,
                "verification_score": 0.95,
                "verification_description": "All checks passed"
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a) POST /endpoint/kimlik_kart_analizi → 200 OK")
    void shouldReturn200ForAnalysisAlias() throws Exception {
        mockMvc.perform(post("/api/v1/kimlik_kart_analizi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ANALYSIS_BODY))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("b) POST /endpoint/kimlik_kart_foto_on → 201 Created, response shape correct")
    void shouldReturn201ForFrontPhotoUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "front.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/kimlik_kart_foto_on")
                        .file(file)
                        .param("tckn", "11111111111"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tckn").value("111******11"))
                .andExpect(jsonPath("$.side").value("FRONT"))
                .andExpect(jsonPath("$.imageId").isNotEmpty());
    }

    @Test
    @DisplayName("c) POST /endpoint/kimlik_kart_foto_arka → 201 Created, response shape correct")
    void shouldReturn201ForBackPhotoUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "back.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/kimlik_kart_foto_arka")
                        .file(file)
                        .param("tckn", "11111111111"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tckn").value("111******11"))
                .andExpect(jsonPath("$.side").value("BACK"))
                .andExpect(jsonPath("$.imageId").isNotEmpty());
    }
}
