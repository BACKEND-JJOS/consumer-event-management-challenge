package com.ias;

import com.ias.exception.CapacityNotFoundException;
import com.ias.gateway.CapacityRepository;
import com.ias.gateway.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdatedCapacityByEventUseCase {

    private final CapacityRepository capacityRepository;
    private final NotificationRepository notificationRepository;

    public Mono<Capacity> execute(Event event, String traceUUID) {

        return capacityRepository.findByEventId(event.getId())
                .switchIfEmpty(Mono.error(new CapacityNotFoundException("Capacity not found for Event ID: " + event.getId())))
                .flatMap(capacity -> {
                    capacity.setLocation(event.getLocation());
                    capacity.setStatus("ACTIVE");
                    return capacityRepository.save(capacity, traceUUID);
                })
                .flatMap(capacityUpdated -> notificationRepository.capacityUpdated(capacityUpdated, traceUUID)
                        .thenReturn(capacityUpdated)
                );
    }
}
