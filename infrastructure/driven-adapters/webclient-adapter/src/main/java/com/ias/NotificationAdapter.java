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

    WebClient webClient;

    private final Gson mapper;

    @Value("${url.notification.service}")
    private String notificationServiceUrl;

    @Override
    public Mono<Capacity> newCapacityCreated(Capacity capacity) {
        return webClient.post()
                .uri(notificationServiceUrl)
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
                                                Mono.error(new RuntimeException("Error: Cambiar a excepción personalizada y capturar"))
                                        )
                )
                .timeout(Duration.ofSeconds(5))
                .doOnNext(capacityFinal -> log.debug("Message Capacity sent successfully: {}", capacityFinal))
                .doOnError(error -> log.error("Error occurred while sending Capacity: {}", error.getMessage()));
    }

}
