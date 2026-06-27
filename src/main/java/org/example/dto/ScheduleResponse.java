package org.example.dto;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        Long groupId,
        Long courseId,
        Long teacherId,
        LocalDateTime dateTime
) {}
