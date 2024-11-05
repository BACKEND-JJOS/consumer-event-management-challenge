package com.ias;

import com.google.gson.Gson;
import com.ias.config.RabbitConsumerConfig;
import com.ias.dto.EventDTO;
import com.ias.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandleEvent {

    private final CreateNewCapacityByEventUseCase createNewCapacityByEventUseCase;

    private final Gson mapper;

    @RabbitListener(queues = RabbitConsumerConfig.EVENT_CREATED_QUEUE)
    public void handleCreateEvent(MessageDTO<EventDTO> messageDTO) {
        createNewCapacityByEventUseCase.execute(
                mapper.fromJson(mapper.toJson(messageDTO.getData()), Event.class),
                messageDTO.getTraceUUID()
        );
    }
}
