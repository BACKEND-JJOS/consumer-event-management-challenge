package com.ias;

import com.ias.model.CapacityEntity;
import com.ias.repository.CapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CapacityReactiveAdapter {

    private final CapacityRepository capacityRepository;

    public Mono<CapacityEntity> save(CapacityEntity capacity){
        return capacityRepository.save(capacity);
    }
}
