package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.Exception.StudentNotFoundException;
import org.example.dao.GroupRepository;
import org.example.dao.StudentRepository;
import org.example.dto.StudentRequest;
import org.example.dto.StudentResponse;
import org.example.model.Group;
import org.example.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public StudentResponse addStudent(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setSurname(request.getSurname());
        student = studentRepository.save(student);

        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(request.getName());
        response.setSurname(request.getSurname());
        response.setGroupId(null);

        return response;
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("студент не найден" + id));
        studentRepository.delete(student);
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("студент не найден" + id));

        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setSurname(student.getSurname());
        if (student.getGroup() != null) {
            response.setGroupId(student.getGroup().getId());
        } else {
            response.setGroupId(null);
        }
    }

    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<StudentResponse> responses = new ArrayList<>();

        for (Student student : students ) {
            StudentResponse response = new StudentResponse();
            response.setId(student.getId());
            response.setName(student.getName());
            response.setSurname(student.getSurname());
            if (student.getGroup() != null) {
                response.setGroupId(student.getGroup().getId());
            } else {
                response.setGroupId(null);
            }
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("студент не найден" + id));
        if (request.getName() != null) {
            student.setName(request.getName());
        }
        if (request.getSurname() != null) {
            student.setSurname(request.getSurname());
        }
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
            student.setGroup(group);
        }
        student = studentRepository.save(student);

        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setSurname(student.getSurname());
        if (student.getGroup() != null) {
            response.setGroupId(student.getGroup().getId());
        } else response.setGroupId(null);

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
            StudentResponse response = new StudentResponse();
            response.setId(student.getId());
            response.setName(student.getName());
            response.setSurname(student.getSurname());
            response.setGroupId(student.getGroup().getId());

            responses.add(response);
        }
        return responses;
    }






}
