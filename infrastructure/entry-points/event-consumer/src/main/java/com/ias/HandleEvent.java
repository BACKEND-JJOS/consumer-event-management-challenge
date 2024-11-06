package com.ias;

import com.google.gson.Gson;
import com.ias.config.RabbitConsumerConfig;
import com.ias.dto.EventDTO;
import com.ias.dto.MessageDTO;
import com.ias.exception.CapacityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandleEvent {

    private final CreateNewCapacityByEventUseCase createNewCapacityByEventUseCase;

    private final UpdatedCapacityByEventUseCase updatedCapacityByEventUseCase;
    private final DeletedCapacityByEventUseCase deletedCapacityByEventUseCase;

    private final Gson mapper;

    @RabbitListener(queues = RabbitConsumerConfig.EVENT_CREATED_QUEUE)
    public Mono<Void> handleCreateEvent(MessageDTO<EventDTO> messageDTO) {
        log.debug("RABBIT RUN {} WITH TRACE {}", RabbitConsumerConfig.EVENT_CREATED_QUEUE, messageDTO.getTraceUUID());
        return createNewCapacityByEventUseCase.execute(
                        mapper.fromJson(mapper.toJson(messageDTO.getData()), Event.class),
                        messageDTO.getTraceUUID()
                )
                .doOnSuccess(unused -> log.info("CAPACITY CREATED SUCCESSFULLY"))
                .doOnError(error -> log.error("ERROR CREATING THE CAPACITY : {}", error))
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .doAfterRetry(retrySignal ->
                                        log.warn("RETRY {}  ERROR  SIGNAl {}", retrySignal.totalRetries(), retrySignal.failure())
                                )
                )
                .onErrorResume(e -> {
                    log.error("UNRECOVERABLE ERROR {}", e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    @RabbitListener(queues = RabbitConsumerConfig.EVENT_UPDATED_QUEUE)
    public Mono<Void> handleUpdatedEvent(MessageDTO<EventDTO> messageDTO) {
        log.debug("RABBIT RUN {} WITH TRACE {}", RabbitConsumerConfig.EVENT_UPDATED_QUEUE, messageDTO.getTraceUUID());
        return updatedCapacityByEventUseCase.execute(
                        mapper.fromJson(mapper.toJson(messageDTO.getData()), Event.class),
                        messageDTO.getTraceUUID()
                )
                .doOnSuccess(unused -> log.info("CAPACITY UPDATED SUCCESSFULLY WITH TRACE {}", messageDTO.getTraceUUID()))
                .doOnError(error -> log.error("ERROR UPDATING  THE CAPACITY : {}", error))
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(error -> !(error instanceof CapacityNotFoundException))
                                .doAfterRetry(retrySignal ->
                                        log.warn("RETRY {}  ERROR  SIGNAl {} WITH TRACE {}", retrySignal.totalRetries(), retrySignal.failure(), messageDTO.getTraceUUID())
                                )
                )
                .onErrorResume(e -> {
                    log.error("UNRECOVERABLE ERROR {} WITH TRACE {}", e.getMessage(), messageDTO.getTraceUUID());
                    return Mono.empty();
                })
                .then();
    }

    @RabbitListener(queues = RabbitConsumerConfig.EVENT_DELETED_QUEUE)
    public Mono<Void> handleDeletedEvent(MessageDTO<EventDTO> messageDTO) {
        log.debug("RABBIT RUN {} WITH TRACE {}", RabbitConsumerConfig.EVENT_DELETED_QUEUE, messageDTO.getTraceUUID());
        return deletedCapacityByEventUseCase.execute(
                        mapper.fromJson(mapper.toJson(messageDTO.getData()), Event.class),
                        messageDTO.getTraceUUID()
                )
                .flatMap(message -> {
                            log.info("CAPACITY DELETED SUCCESSFULLY {} WITH TRACE {}", message, messageDTO.getTraceUUID());
                            return Mono.empty();
                        }
                )
                .doOnError(error -> log.error("ERROR CREATING THE CAPACITY : {}", error))
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .doAfterRetry(retrySignal ->
                                        log.warn("RETRY ATTEMPT #{} DUE TO ERROR: {} WITH TRACE {}", retrySignal.totalRetries(), retrySignal.failure(), messageDTO.getTraceUUID())
                                )
                )
                .onErrorResume(e -> {
                    log.error("UNRECOVERABLE ERROR {} WITH TRACE {}", e.getMessage(), messageDTO.getTraceUUID());
                    return Mono.empty();
                })
                .then();
    }
}
