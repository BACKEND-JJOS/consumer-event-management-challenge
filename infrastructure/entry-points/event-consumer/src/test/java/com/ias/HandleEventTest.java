package com.ias;

import com.google.gson.Gson;
import com.ias.dto.EventDTO;
import com.ias.dto.MessageDTO;
import com.ias.exception.CapacityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandleEventTest {

    @Mock
    private CreateNewCapacityByEventUseCase createNewCapacityByEventUseCase;

    @Mock
    private UpdatedCapacityByEventUseCase updatedCapacityByEventUseCase;

    @Mock
    private DeletedCapacityByEventUseCase deletedCapacityByEventUseCase;

    @InjectMocks
    private HandleEvent handleEvent;

    @Mock
    private Gson mapper;

    @Test
    void handleCreateEvent_shouldRetryOnFailure() {
        MessageDTO<EventDTO> messageDTO = new MessageDTO<>();
        messageDTO.setTraceUUID("trace-uuid");
        when(createNewCapacityByEventUseCase.execute(any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Test Exception")));

        Mono<Void> result = handleEvent.handleCreateEvent(messageDTO);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(createNewCapacityByEventUseCase, times(4)).execute(any(), anyString());
    }

    @Test
    void handleCreateEvent_shouldCompleteSuccessfully() {
        EventDTO eventDTO = EventDTO.builder()
                .id(1)
                .location("any location")
                .date("any-date")
                .name("event-name-mock")
                .build();

        Event event = Event.builder()
                .id(1)
                .location("any location")
                .date("any-date")
                .name("event-name-mock")
                .build();
        String event_json = "{\"id\":1,\"location\":\"any location\",\"date\":\"any-date\",\"name\":\"event-name-mock\"}";


        Capacity capacity = Capacity.builder()
                .id(1)
                .numberAssistant(0)
                .eventId(1)
                .location("any-location")
                .status("ACTIVE")
                .build();

        MessageDTO<EventDTO> messageDTO = new MessageDTO<>();
        messageDTO.setData(eventDTO);
        messageDTO.setTraceUUID("trace-uuid");

        when(mapper.toJson(eventDTO))
                .thenReturn(event_json);
        when(mapper.fromJson(any(String.class), eq(Event.class)))
                .thenReturn(event);

        when(createNewCapacityByEventUseCase.execute(any(), anyString()))
                .thenReturn(Mono.just(capacity));

        Mono<Void> result = handleEvent.handleCreateEvent(messageDTO);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(createNewCapacityByEventUseCase, times(1)).execute(any(), anyString());
    }

    @Test
    void handleUpdatedEvent_shouldHandleCapacityNotFoundException() {
        MessageDTO<EventDTO> messageDTO = new MessageDTO<>();
        messageDTO.setTraceUUID("trace-uuid");

        when(updatedCapacityByEventUseCase.execute(any(), anyString()))
                .thenReturn(Mono.error(new CapacityNotFoundException("Capacity not found")));

        Mono<Void> result = handleEvent.handleUpdatedEvent(messageDTO);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(updatedCapacityByEventUseCase, times(1)).execute(any(), anyString());
    }
}
