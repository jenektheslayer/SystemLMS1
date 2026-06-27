package org.example.dto;

import java.util.List;

public record AssignGroupRequest(List<Long> studentsIds, Long groupId) {}
