package org.example.mapper;

import org.example.dto.GroupRequest;
import org.example.dto.GroupResponse;
import org.example.dto.StudentResponse;
import org.example.model.Group;
import org.example.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupResponse toDto(Group group);

    Group toEntity(GroupRequest request);

    void updateEntity(GroupRequest request, @MappingTarget Group group);

    List<GroupResponse> toDtoList(List<Group> groups);

}
