package com.ias.gateway;

import com.ias.Capacity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityRepository {
    Mono<Capacity> save(Capacity capacity, String traceUUID);

    Mono<Void> deletedByEventId(Integer eventId, String traceUUID);

    Mono<Capacity> findByEventId(Integer eventId);
}
