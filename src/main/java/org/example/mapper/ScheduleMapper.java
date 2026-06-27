package org.example.mapper;

import org.example.dto.ScheduleRequest;
import org.example.dto.ScheduleResponse;
import org.example.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleResponse toDto(Schedule schedule);

    Schedule toEntity(ScheduleRequest request);

    void updateEntity(ScheduleRequest request, @MappingTarget Schedule schedule);

    List<ScheduleResponse> toDtoList(List<Schedule> schedules);
}
