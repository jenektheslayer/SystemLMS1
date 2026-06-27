package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.Exception.StudentNotFoundException;
import org.example.dao.GroupRepository;
import org.example.dao.StudentRepository;
import org.example.dto.PagedResponse;
import org.example.dto.StudentRequest;
import org.example.dto.StudentResponse;
import org.example.mapper.StudentMapper;
import org.example.model.Group;
import org.example.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse addStudent(StudentRequest request) {

        Long groupId = request.groupId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        Student student = studentMapper.toEntity(request);
        student.setGroup(group);
        student = studentRepository.save(student);

        StudentResponse response = studentMapper.toDto(student);

        return response;
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("студент не найден" + id));

        StudentResponse response = studentMapper.toDto(student);

        return response;
    }

    public PagedResponse<StudentResponse> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        List<Student> students = studentPage.getContent();
        List<StudentResponse> content = studentMapper.toDtoList(students);
        return new PagedResponse<>(
                content,
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages()
        );
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("студент не найден" + id));
        studentMapper.updateEntity(request, student);
        if (request.groupId() != null) {
            Group group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
            student.setGroup(group);
        }
        student = studentRepository.save(student);
        StudentResponse response = studentMapper.toDto(student);

        return response;
    }

    @Transactional
    public List<StudentResponse> assignStudentsToGroup(List<Long> studentIds, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
        List<StudentResponse> responses = new ArrayList<>();
        for (Long studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException("студент не найден"));
            if (student.getGroup() == null) {
                student.setGroup(group);
            } else {
                throw new StudentAlreadyInGroupException("Студент уже находится в другой группе")
            }
            student = studentRepository.save(student);
            StudentResponse response = studentMapper.toDto(student);

            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("студент не найден" + id));
        studentRepository.delete(student);
    }
}