package com.example.fileproc.api;

import com.example.fileproc.infrastructure.persistence.RequestLogEntity;
import com.example.fileproc.infrastructure.persistence.RequestLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock
class FileProcessingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @InjectWireMock
    private com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    private static final String VALID_FILE_CONTENT = """
            18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
            3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
            """;

    private static final String INVALID_UUID_FILE_CONTENT = """
            not-a-uuid|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
            """;

    @BeforeEach
    void setUp() {
        requestLogRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @Test
    void processFile_withAllowedIp_returns200AndCorrectOutput() throws Exception {
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

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"OutcomeFile.json\""))
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[0].transport").value("Rides A Bike"))
                .andExpect(jsonPath("$[0].topSpeed").value(12.1))
                .andExpect(jsonPath("$[1].name").value("Mike Smith"))
                .andExpect(jsonPath("$[1].transport").value("Drives an SUV"))
                .andExpect(jsonPath("$[1].topSpeed").value(95.5));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());

        RequestLogEntity log = logs.get(0);
        assertEquals(200, log.getHttpResponseCode());
        assertEquals("GB", log.getRequestCountryCode());
        assertEquals("BT", log.getRequestIpProvider());
        assertTrue(log.getTimeLapsedMs() >= 0);
    }

    @Test
    void processFile_withBlockedCountry_returns403() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": "success",
                                    "country": "United States",
                                    "countryCode": "US",
                                    "isp": "Comcast",
                                    "query": "1.2.3.4"
                                }
                                """)));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "1.2.3.4"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.reason").value("Blocked country: US"));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(403, logs.get(0).getHttpResponseCode());
        assertEquals("US", logs.get(0).getRequestCountryCode());
    }

    @Test
    void processFile_withBlockedIsp_returns403() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": "success",
                                    "country": "Germany",
                                    "countryCode": "DE",
                                    "isp": "Amazon AWS",
                                    "query": "1.2.3.4"
                                }
                                """)));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "1.2.3.4"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.reason").value("Blocked ISP: Amazon AWS"));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(403, logs.get(0).getHttpResponseCode());
    }

    @Test
    void processFile_withIpApiFailPayload_returns403AndIncludesFailReason() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": "fail",
                                    "message": "private range",
                                    "query": "192.168.1.1"
                                }
                                """)));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("private range"));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(403, logs.get(0).getHttpResponseCode());
    }

    @Test
    void processFile_withInvalidFile_returns400_includesRequestId_andIsAudited() throws Exception {
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

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                INVALID_UUID_FILE_CONTENT.getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.requestId").exists())
                .andReturn();

        String headerId = result.getResponse().getHeader("X-Request-Id");
        assertNotNull(headerId);

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains(headerId));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(400, logs.get(0).getHttpResponseCode());
    }

    @Test
    void processFile_withValidationEnabled_rejectsInvalidUuid_returns400() throws Exception {
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

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                INVALID_UUID_FILE_CONTENT.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/outcome-file")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        List<RequestLogEntity> logs = requestLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(400, logs.get(0).getHttpResponseCode());
    }
}
