package org.example.controller.controllerTest;

import org.example.controller.AbstractIT;
import org.example.dao.CourseRepository;
import org.example.dao.GroupRepository;
import org.example.dao.ScheduleRepository;
import org.example.dao.TeacherRepository;
import org.example.dto.PagedResponse;
import org.example.dto.ScheduleRequest;
import org.example.dto.ScheduleResponse;
import org.example.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class ScheduleControllerTest extends AbstractIT {


    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    void shouldCreateSchedule() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        ScheduleRequest request = new ScheduleRequest(
                group.getId(),
                course.getId(),
                LocalDateTime.of(2026, 7, 2, 14, 0)
        );

        ResponseEntity<ScheduleResponse> response = restTemplate.postForEntity(
                url("/api/v1/schedules"),
                request,
                ScheduleResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void shouldGetScheduleById() {

        // given (создаём СВОИ данные, не используем чужие)
        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        schedule = scheduleRepository.save(schedule);

        ResponseEntity<ScheduleResponse> response = restTemplate.getForEntity(
                url("/api/v1/schedules/" + schedule.getId()),
                ScheduleResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(schedule.getId());
    }

    @Test
    void shouldUpdateSchedule() {

        // given
        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        schedule = scheduleRepository.save(schedule);

        // update payload
        ScheduleRequest request = new ScheduleRequest(
                group.getId(),
                course.getId(),
                LocalDateTime.of(2026, 7, 3, 9, 0)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        HttpEntity<ScheduleRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ScheduleResponse> response = restTemplate.exchange(
                url("/api/v1/schedules/" + schedule.getId()),
                HttpMethod.PUT,
                entity,
                ScheduleResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().dateTime())
                .isEqualTo("2026-07-03T09:00:00");
    }

    @Test
    void shouldDeleteSchedule() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        schedule = scheduleRepository.save(schedule);

        Long id = schedule.getId();

        restTemplate.delete(url("/api/v1/schedules/" + id));

        assertThat(scheduleRepository.existsById(id)).isFalse();
    }

    @Test
    void shouldGetSchedulesByGroupId() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        scheduleRepository.save(schedule);

        ResponseEntity<PagedResponse> response = restTemplate.getForEntity(
                url("/api/v1/schedules/by-group/" + group.getId() + "?page=0&size=10"),
                PagedResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalElements()).isEqualTo(1);
    }

    @Test
    void shouldGetSchedulesByTeacherId() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        scheduleRepository.save(schedule);

        ResponseEntity<PagedResponse> response = restTemplate.getForEntity(
                url("/api/v1/schedules/by-teacher/" + teacher.getId() + "?page=0&size=10"),
                PagedResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalElements()).isEqualTo(1);
    }

    @Test
    void shouldNotCreateScheduleWhenTeacherBusy() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        LocalDateTime time = LocalDateTime.of(2026, 7, 1, 10, 0);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(time);
        scheduleRepository.save(schedule);

        ScheduleRequest request = new ScheduleRequest(
                group.getId(),
                course.getId(),
                time
        );

        try {
            restTemplate.postForEntity(url("/api/v1/schedules"), request, String.class);
            fail("Expected CONFLICT");
        } catch (HttpClientErrorException.Conflict e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Test
    void shouldGetAllSchedules() {

        Teacher teacher = new Teacher();
        teacher.setName("Сергей");
        teacher.setSurname("Иванов");
        teacher = teacherRepository.save(teacher);

        Group group = new Group();
        group.setName("Java-21");
        group = groupRepository.save(group);

        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Intro");
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setDateTime(LocalDateTime.of(2026, 7, 1, 10, 0));
        scheduleRepository.save(schedule);

        ResponseEntity<PagedResponse> response = restTemplate.getForEntity(
                url("/api/v1/schedules?page=0&size=10"),
                PagedResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<Object> content = response.getBody().content();

        boolean found = false;

        for (Object item : content) {
            Map<?, ?> map = (Map<?, ?>) item;

            Object groupId = map.get("groupId");

            if (groupId != null && groupId.equals(group.getId().intValue())) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }
}