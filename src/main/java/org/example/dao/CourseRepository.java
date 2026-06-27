package org.example.dao;

import org.example.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTeacherId(Long teacherId);
}