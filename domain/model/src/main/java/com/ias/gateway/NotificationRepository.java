package com.ias.gateway;

import com.ias.Capacity;
import com.ias.Event;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository {
    Mono<Capacity> newCapacityCreated(Capacity capacity, String traceUUID);

    Mono<String> capacityDeleted(Event event, String traceUUID);

    Mono<String> capacityUpdated(Capacity capacity, String traceUUID);


}
