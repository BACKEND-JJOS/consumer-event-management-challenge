package com.ias;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Capacity {
    private Integer id;
    private String status;
    private Integer eventId;
    private String location;
    private Integer numberAssistant;
}
