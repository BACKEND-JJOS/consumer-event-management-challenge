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
public class DeletedCapacityByEventUseCase {

    private final CapacityRepository capacityRepository;
    private final NotificationRepository notificationRepository;

    public Mono<String> execute(Event event, String traceUUID) {
        return capacityRepository.deletedByEventId(event.getId(), traceUUID)
                .flatMap(capacity ->
                        notificationRepository.capacityDeleted(event, traceUUID)
                );
    }
}
