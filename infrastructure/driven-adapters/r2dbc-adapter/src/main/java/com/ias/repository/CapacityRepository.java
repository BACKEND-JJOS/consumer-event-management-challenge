package com.ias.repository;

import com.ias.model.CapacityEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityRepository extends R2dbcRepository<CapacityEntity, Integer> {
    Mono<Void> deleteByEventId(Integer eventId);

    Mono<CapacityEntity> findByEventId(Integer eventId);
}
