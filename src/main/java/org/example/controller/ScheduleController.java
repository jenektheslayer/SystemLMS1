package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.Service.ScheduleService;
import org.example.dto.PagedResponse;
import org.example.dto.ScheduleRequest;
import org.example.dto.ScheduleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> addSchedule(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.addSchedule(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ScheduleResponse>> getAllSchedule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
    return ResponseEntity.ok(scheduleService.getAllSchedules(page, size));
    }

    @GetMapping("/by-group/{groupId}")
    public ResponseEntity<PagedResponse<ScheduleResponse>> getAllScheduleByGroupId(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
        return ResponseEntity.ok(scheduleService.getSchedulesByGroupId(groupId, page, size));
    }

    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<PagedResponse<ScheduleResponse>> getAllScheduleByTeacherId(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
        return ResponseEntity.ok(scheduleService.getSchedulesByTeacherId(teacherId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

}
