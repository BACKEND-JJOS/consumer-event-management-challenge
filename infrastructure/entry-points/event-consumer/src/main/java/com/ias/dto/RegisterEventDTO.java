package com.ias.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterEventDTO {
    private Integer userId;
    private Integer eventId;
}
