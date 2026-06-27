package org.example.dao;

import org.example.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByCourseId(Long courseId);
    Page<Schedule> findByGroupId(Long groupId, Pageable pageable);
    Page<Schedule> findByTeacherId(Long teacherId, Pageable pageable);
}
