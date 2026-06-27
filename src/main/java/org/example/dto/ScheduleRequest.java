package org.example.dto;

import java.time.LocalDateTime;

public record ScheduleRequest(Long groupId, Long courseId, LocalDateTime dateTime) {}
