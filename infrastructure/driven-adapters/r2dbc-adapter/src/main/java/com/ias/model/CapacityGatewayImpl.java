package com.ias.model;

import com.google.gson.Gson;
import com.ias.Capacity;
import com.ias.CapacityReactiveAdapter;
import com.ias.gateway.CapacityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CapacityGatewayImpl implements CapacityRepository {

    private final CapacityReactiveAdapter capacityReactiveAdapter;
    private final Gson mapper;

    private static final String MESSAGE_LOG_TRACE = "ADAPTER DATABASE RUN {} WITH TRACE {}";

    @Override
    public Mono<Capacity> save(Capacity capacity, String traceUUID) {
        return capacityReactiveAdapter.save(
                        mapper.fromJson(
                                mapper.toJson(capacity),
                                CapacityEntity.class)
                )
                .map(capacitySaved -> mapper.fromJson(mapper.toJson(capacitySaved), Capacity.class))
                .doOnSubscribe(subscription -> log.debug(MESSAGE_LOG_TRACE, "save", traceUUID))
                .doOnError(error -> log.error(MESSAGE_LOG_TRACE, error, traceUUID));
    }

    @Override
    public Mono<Void> deletedByEventId(Integer eventId, String traceUUID) {
        return capacityReactiveAdapter.deletedByEventId(eventId)
                .doOnSubscribe(subscription -> log.debug(MESSAGE_LOG_TRACE, "save", traceUUID))
                .doOnError(error -> log.error(MESSAGE_LOG_TRACE, error, traceUUID));
    }

    @Override
    public Mono<Capacity> findByEventId(Integer eventId) {
        return capacityReactiveAdapter.findByEventId(eventId)
                .map(capacityEntity ->  mapper.fromJson(mapper.toJson(capacityEntity), Capacity.class));
    }
}
