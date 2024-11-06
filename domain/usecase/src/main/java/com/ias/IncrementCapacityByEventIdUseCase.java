package com.ias;

import com.ias.gateway.CapacityRepository;
import com.ias.gateway.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class IncrementCapacityByEventIdUseCase {

    private final CapacityRepository capacityRepository;

    private final NotificationRepository notificationRepository;

    public Mono<Capacity> execute(Integer eventId, String traceUUID) {
        log.info("eventtttttttttttttttttttt {}",eventId);
        return capacityRepository.findByEventId(eventId)
                .flatMap(capacity -> {
                    capacity.setNumberAssistant(capacity.getNumberAssistant() + 1);
                    return capacityRepository.save(capacity, traceUUID);
                })
                .flatMap(capacityUpdated ->
                        notificationRepository
                                .capacityUpdated(capacityUpdated, traceUUID)
                                .thenReturn(capacityUpdated)
                );
    }
}
