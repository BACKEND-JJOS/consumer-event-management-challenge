package com.ias.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO<T> {
    private String traceUUID;
    private T data;
}

