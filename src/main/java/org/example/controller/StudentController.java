package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.Service.StudentService;
import org.example.dto.AssignGroupRequest;
import org.example.dto.PagedResponse;
import org.example.dto.StudentRequest;
import org.example.dto.StudentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> addStudent(@RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.addStudent(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
        return ResponseEntity.ok(studentService.getAllStudents(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequest request)
    {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-group")
    public ResponseEntity<List<StudentResponse>> assignStudentsToGroup(
            @RequestBody AssignGroupRequest request) {
        return ResponseEntity.ok(
                studentService.assignStudentsToGroup(request.studentsIds(), request.groupId())
        );
    }
}
