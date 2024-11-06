package com.ias.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("capacity")
public class CapacityEntity {
    @Id
    private Integer id;
    private String status;
    private Integer eventId;
    private String location;
    private Integer numberAssistant;
}
