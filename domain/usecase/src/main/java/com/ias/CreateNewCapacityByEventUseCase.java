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
public class CreateNewCapacityByEventUseCase {

    private final CapacityRepository capacityRepository;
    private final NotificationRepository notificationRepository;

    public Mono<Capacity> execute(Event event, String traceUUID) {

        return capacityRepository.save(
                        Capacity.builder()
                                .eventId(event.getId())
                                .location(event.getLocation())
                                .numberAssistant(0)
                                .status("ACTIVE")
                                .build()
                        , traceUUID
                )
                .flatMap(capacity -> notificationRepository.newCapacityCreated(capacity, traceUUID));
    }
}
