package org.example.mapper;

import org.example.dto.CourseRequest;
import org.example.dto.CourseResponse;
import org.example.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseResponse toDto(Course course);

    Course toEntity(CourseRequest request);

    void updateEntity(CourseRequest request, @MappingTarget Course course);

    List<CourseResponse> toDtoList(List<Course> courses);

}
