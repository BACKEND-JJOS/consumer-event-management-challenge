package com.ias;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Event{

    private Integer id;
    private String name;
    private String date;
    private String location;
}
