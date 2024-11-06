package com.ias;

import com.ias.model.CapacityEntity;
import com.ias.repository.CapacityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CapacityReactiveAdapter {

    private final CapacityRepository capacityRepository;

    public Mono<CapacityEntity> save(CapacityEntity capacity) {
        return capacityRepository.save(capacity);
    }

    public Mono<Void> deletedByEventId(Integer eventId) {
        return capacityRepository.deleteByEventId(eventId);
    }

    public Mono<CapacityEntity> findByEventId(Integer eventId) {
        return capacityRepository.findByEventId(eventId);
    }
}
