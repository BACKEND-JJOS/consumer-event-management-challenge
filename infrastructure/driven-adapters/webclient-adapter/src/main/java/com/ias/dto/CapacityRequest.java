package com.ias.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CapacityRequest {
    private Integer id;
    private String status;
    private Integer eventId;
    private String location;
    private Integer numberAssistant;
}

