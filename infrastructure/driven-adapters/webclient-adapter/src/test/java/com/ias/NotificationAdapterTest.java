package com.ias;

import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
public class NotificationAdapterTest {

    private MockWebServer mockWebServer;
    private NotificationAdapter notificationAdapter;
    private WebClient webClient;


    @BeforeEach
    public void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        notificationAdapter = new NotificationAdapter(webClient, new Gson());
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testNewCapacityCreated() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\": 1, \"status\": \"created\", \"eventId\": 123, \"location\": \"Test Location\", \"numberAssistant\": 0}")
                .addHeader("Content-Type", "application/json"));

        Capacity capacity = new Capacity(1, "created", 123, "Test Location", 10);
        String traceUUID = "trace-123";

        Mono<Capacity> responseMono = notificationAdapter.newCapacityCreated(capacity, traceUUID);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatus().equals("created"))
                .expectComplete()
                .verify();

        mockWebServer.takeRequest();
    }

    @Test
    public void testCapacityDeleted() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Event event = new Event(1, "name-test", "date-test", "location-test");
        String traceUUID = "trace-test";

        Mono<String> responseMono = notificationAdapter.capacityDeleted(event, traceUUID);

        StepVerifier.create(responseMono)
                .expectNext("notification deleted capacity")
                .expectComplete()
                .verify();
    }

    @Test
    void testCapacityDeleted_RuntimeException() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"message\": \"Internal Server Error\"}")
                .addHeader("Content-Type", "application/json"));

        Event event = new Event();  // Configura tu evento según lo necesario
        String traceUUID = "test-trace-uuid";

        Mono<String> result = notificationAdapter.capacityDeleted(event, traceUUID);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }


    @Test
    void testCapacityUpdated() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\": \"updated\", \"eventId\": 123, \"location\": \"Updated Location\", \"numberAssistant\": 15}")
                .addHeader("Content-Type", "application/json"));

        Capacity capacity = Capacity.builder()
                .id(1)
                .status("updated")
                .eventId(123)
                .location("Updated Location")
                .numberAssistant(15)
                .build();

        String traceUUID = "test-trace-uuid";

        Mono<String> result = notificationAdapter.capacityUpdated(capacity, traceUUID);

        StepVerifier.create(result)
                .expectNext("notification updated capacity")
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("application/json", request.getHeader("Content-Type"));

        String requestBody = request.getBody().readUtf8();
        assertTrue(requestBody.contains("\"status\":\"updated\""));
        assertTrue(requestBody.contains("\"eventId\":123"));
        assertTrue(requestBody.contains("\"location\":\"Updated Location\""));
        assertTrue(requestBody.contains("\"numberAssistant\":15"));
    }


}
