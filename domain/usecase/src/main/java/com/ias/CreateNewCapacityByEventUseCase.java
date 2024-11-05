package com.ias;

import com.ias.gateway.CapacityRepository;
import com.ias.gateway.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CreateNewCapacityByEventUseCase {

    private final CapacityRepository capacityRepository;
    //private final NotificationRepository notificationRepository;

    public Mono<Capacity> execute(Event event, String traceUUID) {
        return capacityRepository.save(
                        Capacity.builder()
                                .eventId(event.getId())
                                .location(event.getLocation())
                                .numberAssistant(0)
                                .status("ACTIVE")
                                .build()
                );
      //          .flatMap(notificationRepository::newCapacityCreated);
    }
}
