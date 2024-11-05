package com.ias.gateway;

import com.ias.Capacity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityRepository {
    Mono<Capacity> save(Capacity capacity);
}
