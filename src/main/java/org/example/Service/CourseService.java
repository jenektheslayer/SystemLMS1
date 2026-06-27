package org.example.Service;


import lombok.RequiredArgsConstructor;
import org.example.dao.CourseRepository;
import org.example.dao.ScheduleRepository;
import org.example.dao.TeacherRepository;
import org.example.dto.CourseRequest;
import org.example.dto.CourseResponse;
import org.example.dto.PagedResponse;
import org.example.dto.StudentResponse;
import org.example.mapper.CourseMapper;
import org.example.model.Course;
import org.example.model.Schedule;
import org.example.model.Student;
import org.example.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public CourseResponse addCourse(CourseRequest request) {

        Long teacherId = request.teacherId();
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException());
        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseResponse response = courseMapper.toDto(course);
        return response;
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        CourseResponse response = courseMapper.toDto(course);
        return response;
    }

    public PagedResponse<CourseResponse> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findAll(pageable);
        List<Course> courses = coursePage.getContent();
        List<CourseResponse> content = courseMapper.toDtoList(courses);
        return new PagedResponse<>(
                content,
                coursePage.getNumber(),
                coursePage.getSize(),
                coursePage.getTotalElements(),
                coursePage.getTotalPages()
        );
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new CourseNotFoundException("Курс не найден"));
        courseMapper.updateEntity(request, course);
        if (request.teacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.teacherId())
                    .orElseThrow(()-> new TeacherNotFoundException("Учитель не найден"));
            course.setTeacher(teacher);
        }
        course = courseRepository.save(course);

        CourseResponse response = courseMapper.toDto(course);
        return response;
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new CourseNotFoundException("Курс не найден"));
        if (scheduleRepository.existsByCourseId(id)) {
            throw new CourseHasScheduleException("Невозможно удалить курс, так как он есть в расписании");
        }
        courseRepository.delete(course);
    }
}
