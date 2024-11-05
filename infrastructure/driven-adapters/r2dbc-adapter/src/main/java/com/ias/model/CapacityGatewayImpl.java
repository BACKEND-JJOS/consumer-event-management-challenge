package com.ias.model;

import com.google.gson.Gson;
import com.ias.Capacity;
import com.ias.CapacityReactiveAdapter;
import com.ias.gateway.CapacityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CapacityGatewayImpl implements CapacityRepository {

    private final CapacityReactiveAdapter assistantReactiveAdapter;
    private final Gson mapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return assistantReactiveAdapter.save(
                        mapper.fromJson(mapper.toJson(capacity),
                                CapacityEntity.class))
                .map(capacitySaved -> mapper.fromJson(mapper.toJson(capacitySaved), Capacity.class));
    }
}
