package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponse {
    private Long id;
    private Long groupId;
    private Long courseId;
    private Long teacherId;
    private LocalDateTime dateTime;
}
