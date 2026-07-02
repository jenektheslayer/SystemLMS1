package org.example.mapper;

import org.example.dto.StudentRequest;
import org.example.dto.StudentResponse;
import org.example.dto.TeacherRequest;
import org.example.dto.TeacherResponse;
import org.example.model.Student;
import org.example.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherResponse toDto(Teacher teacher);

    Teacher toEntity(TeacherRequest request);

    void updateEntity(TeacherRequest request, @MappingTarget Teacher teacher);

    List<TeacherResponse> toDtoList(List<Teacher> teachers);

}
