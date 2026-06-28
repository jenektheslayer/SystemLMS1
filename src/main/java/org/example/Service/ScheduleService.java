package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.dao.CourseRepository;
import org.example.dao.GroupRepository;
import org.example.dao.ScheduleRepository;
import org.example.dao.TeacherRepository;
import org.example.dto.PagedResponse;
import org.example.dto.ScheduleRequest;
import org.example.dto.ScheduleResponse;
import org.example.exception.CourseNotFoundException;
import org.example.exception.GroupNotFoundException;
import org.example.exception.ScheduleNotFoundException;
import org.example.exception.TeacherNotAvailableException;
import org.example.mapper.ScheduleMapper;
import org.example.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleMapper scheduleMapper;

    @Transactional
    public ScheduleResponse addSchedule(ScheduleRequest request) {
        Schedule schedule = scheduleMapper.toEntity(request);
        Course course = courseRepository.findById(request.courseId())
                        .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        schedule.setCourse(course);
        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
        schedule.setGroup(group);
        Teacher teacher = course.getTeacher();
        schedule.setTeacher(teacher);
        if (scheduleRepository.existsByTeacherIdAndDateTime(teacher.getId(), request.dateTime())) {
            throw new TeacherNotAvailableException("Учитель занят в это время");
        }
        schedule = scheduleRepository.save(schedule);
        ScheduleResponse response = scheduleMapper.toDto(schedule);
        return response;
    }

    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(()-> new ScheduleNotFoundException("Отсутствует расписание"));
        ScheduleResponse response = scheduleMapper.toDto(schedule);
        return response;
    }

    public PagedResponse<ScheduleResponse> getAllSchedules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findAll(pageable);
        List<Schedule> schedules = schedulePage.getContent();
        List<ScheduleResponse> content = scheduleMapper.toDtoList(schedules);
        return new PagedResponse<>(
                content,
                schedulePage.getNumber(),
                schedulePage.getSize(),
                schedulePage.getTotalElements(),
                schedulePage.getTotalPages()
        );
    }

    public PagedResponse<ScheduleResponse> getSchedulesByGroupId(Long groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByGroupId(groupId, pageable);
        List<Schedule> schedules = schedulePage.getContent();
        List<ScheduleResponse> content = scheduleMapper.toDtoList(schedules);
        return new PagedResponse<>(
                content,
                schedulePage.getNumber(),
                schedulePage.getSize(),
                schedulePage.getTotalElements(),
                schedulePage.getTotalPages()
        );
    }

    public PagedResponse<ScheduleResponse> getSchedulesByTeacherId(Long teacherId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByTeacherId(teacherId, pageable);
        List<Schedule> schedules = schedulePage.getContent();
        List<ScheduleResponse> content = scheduleMapper.toDtoList(schedules);
        return new PagedResponse<>(
                content,
                schedulePage.getNumber(),
                schedulePage.getSize(),
                schedulePage.getTotalElements(),
                schedulePage.getTotalPages()
        );
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Отсутствует расписание"));
        scheduleMapper.updateEntity(request, schedule);

        if (request.groupId() != null) {
            Group group = groupRepository.findById(request.groupId())
                    .orElseThrow(()-> new GroupNotFoundException("Группа не найдена"));
            schedule.setGroup(group);
        }
        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
            schedule.setCourse(course);
            schedule.setTeacher(course.getTeacher());
        }
        if (scheduleRepository.existsByTeacherIdAndDateTimeAndIdNot(
                schedule.getTeacher().getId(),
                schedule.getDateTime(),
                id))
        {
            throw new TeacherNotAvailableException("Учитель занят в это время");
        }
        schedule = scheduleRepository.save(schedule);

        ScheduleResponse response = scheduleMapper.toDto(schedule);
        return response;
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Отсутствует расписание"));
        scheduleRepository.delete(schedule);
    }
}
