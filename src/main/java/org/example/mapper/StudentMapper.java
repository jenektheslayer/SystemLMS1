package org.example.mapper;

import org.example.dto.StudentRequest;
import org.example.dto.StudentResponse;
import org.example.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentResponse toDto(Student student);

    Student toEntity(StudentRequest request);

    void updateEntity(StudentRequest request, @MappingTarget Student student);

    List<StudentResponse> toDtoList(List<Student> students);
}
