package com.ias.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EventDTO {

    private Integer id;
    private String name;
    private String date;
    private String location;
}

