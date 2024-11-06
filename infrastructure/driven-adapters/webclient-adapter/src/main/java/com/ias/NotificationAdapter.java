package com.ias;

import com.google.gson.Gson;
import com.ias.dto.CapacityRequest;
import com.ias.gateway.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAdapter implements NotificationRepository {

    private final WebClient webClient;

    private final Gson mapper;

    private static final String MESSAGE_LOG_TRACE = "ADAPTER WEB CLIENT RUN {} WITH TRACE {}";

    @Value("${url.notification.service}")
    private String notificationServiceUrl;

    @Override
    public Mono<Capacity> newCapacityCreated(Capacity capacity, String traceUUID) {
        return webClient.post()
                .uri(notificationServiceUrl + "/created")
                .body(BodyInserters.fromValue(
                                mapper.fromJson(
                                        mapper.toJson(capacity),
                                        CapacityRequest.class)
                        )
                )
                .exchangeToMono(response ->
                        !response.statusCode().isError() ?
                                response.bodyToMono(Capacity.class) :

                                response.bodyToMono(ProblemDetail.class)
                                        .flatMap(errorMessage ->
                                                Mono.error(new RuntimeException("Error" + errorMessage))
                                        )
                )
                .timeout(Duration.ofSeconds(5))
                .doOnNext(capacity1 -> log.debug(MESSAGE_LOG_TRACE, "newCapacityCreated", traceUUID))
                .doOnError(error -> log.error("ADAPTER WEB CLIENT Error occurred while sending Capacity: {}", error.getMessage()));
    }

    @Override
    public Mono<String> capacityDeleted(Event event, String traceUUID) {
        return webClient.post()
                .uri(notificationServiceUrl + "/deleted")
                .body(BodyInserters.fromValue(event))
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is5xxServerError(),
                        response -> {
                            log.error("500 SERVER ERROR DURING CAPACITY DELETED NOTIFICATION for trace {}", traceUUID);
                            return Mono.error(new RuntimeException("Service unavailable, 500 error"));
                        }
                )
                .bodyToMono(Void.class)
                .thenReturn("notification deleted capacity")
                .doOnSuccess(message -> log.debug(MESSAGE_LOG_TRACE, message, traceUUID))
                .doOnError(error -> log.error("ADAPTER WEB CLIENT Error occurred while sending Capacity: {}", error.getMessage()));
    }

    @Override
    public Mono<String> capacityUpdated(Capacity capacity, String traceUUID) {
        return webClient.post()
                .uri(notificationServiceUrl + "/updated")
                .body(BodyInserters.fromValue(capacity))
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is5xxServerError(),
                        response -> {
                            log.error("500 SERVER ERROR DURING CAPACITY UPDATED NOTIFICATION for trace {}", traceUUID);
                            return Mono.error(new RuntimeException("Service unavailable, 500 error"));
                        }
                )
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError(),
                        clientResponse -> {
                            log.error("400 CLIENT ERROR DURING CAPACITY UPDATED NOTIFICATION for trace {}", traceUUID);
                            return Mono.error(new RuntimeException("Service error, 400 error"));
                        }
                )
                .bodyToMono(Void.class)
                .thenReturn("notification updated capacity")
                .doOnSuccess(message -> log.debug(MESSAGE_LOG_TRACE, message, traceUUID))
                .doOnError(error -> log.error("ADAPTER WEB CLIENT Error occurred while sending Capacity: {}", error.getMessage()));
    }

}
