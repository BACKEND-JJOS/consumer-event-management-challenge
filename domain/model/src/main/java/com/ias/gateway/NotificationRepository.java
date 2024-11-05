package com.ias.gateway;

import com.ias.Capacity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository {
    Mono<Capacity> newCapacityCreated(Capacity capacity);
}
