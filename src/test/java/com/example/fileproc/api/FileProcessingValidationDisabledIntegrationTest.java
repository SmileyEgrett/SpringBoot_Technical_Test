package com.example.fileproc.api;

import com.example.fileproc.infrastructure.persistence.RequestLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "features.fileValidation.enabled=false")
@AutoConfigureMockMvc
@EnableWireMock
class FileProcessingValidationDisabledIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @InjectWireMock
    private com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        requestLogRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @Test
    void processFile_withValidationDisabled_allowsInvalidUuidAndAvgSpeed_returns200() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": "success",
                                    "country": "United Kingdom",
                                    "countryCode": "GB",
                                    "isp": "BT",
                                    "query": "8.8.8.8"
                                }
                                """)));

        String invalidUuidAndAvgSpeedContent = """
                not-a-uuid|1X1D14|John Smith|Likes Apricots|Rides A Bike|not-a-number|12.1
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                invalidUuidAndAvgSpeedContent.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[0].transport").value("Rides A Bike"))
                .andExpect(jsonPath("$[0].topSpeed").value(12.1));
    }

    @Test
    void processFile_withValidationDisabled_invalidTopSpeed_returns400() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": "success",
                                    "country": "United Kingdom",
                                    "countryCode": "GB",
                                    "isp": "BT",
                                    "query": "8.8.8.8"
                                }
                                """)));

        String invalidTopSpeedContent = """
                not-a-uuid|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|not-a-speed
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                invalidTopSpeedContent.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}
