package org.example.Service;

import lombok.*;
import org.example.dao.CourseRepository;
import org.example.dao.TeacherRepository;
import org.example.dto.PagedResponse;
import org.example.dto.TeacherRequest;
import org.example.dto.TeacherResponse;
import org.example.exception.TeacherHasCoursesException;
import org.example.exception.TeacherNotFoundException;
import org.example.mapper.TeacherMapper;
import org.example.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TeacherMapper teacherMapper;

    @Transactional
    public TeacherResponse addTeacher(TeacherRequest request) {

        Teacher teacher = teacherMapper.toEntity(request);
        teacher = teacherRepository.save(teacher);

        TeacherResponse response = teacherMapper.toDto(teacher);

        return response;
    }

    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Учитель не найден"));

        TeacherResponse response = teacherMapper.toDto(teacher);
        return response;
    }



    public PagedResponse<TeacherResponse> getAllTeachers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Teacher> teacherPage = teacherRepository.findAll(pageable);
        List<Teacher> teachers = teacherPage.getContent();
        List<TeacherResponse> content = teacherMapper.toDtoList(teachers);
        return new PagedResponse<>(
                content,
                teacherPage.getNumber(),
                teacherPage.getSize(),
                teacherPage.getTotalElements(),
                teacherPage.getTotalPages()
        );
    }

    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                        .orElseThrow(() -> new TeacherNotFoundException("Учитель не найден"));
        teacherMapper.updateEntity(request, teacher);
        teacherRepository.save(teacher);
        TeacherResponse response = teacherMapper.toDto(teacher);
        return response;
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Учитель не найден"));
        if (courseRepository.existsByTeacherId(id)) {
            throw new TeacherHasCoursesException("Нельзя удалить, так как учитель ведет курсы");
        }
        teacherRepository.delete(teacher);
    }


}
