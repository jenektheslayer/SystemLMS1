package org.example.controller;

import org.example.dao.TeacherRepository;
import org.example.dto.PagedResponse;
import org.example.dto.TeacherRequest;
import org.example.dto.TeacherResponse;
import org.example.model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TeacherControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("integration-tests-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.yml");
    }

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
        Teacher teacher = new Teacher();
        teacher.setName("Андрей");
        teacher.setSurname("Андреев");
        teacherRepository.save(teacher);
    }

    @AfterEach
    void tearDown() {
        teacherRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + "/api/v1/teachers" + path;
    }

    @Test
    void shouldCreateTeacher() {
        TeacherRequest request = new TeacherRequest("Татьяна", "Каратаева");

        ResponseEntity<TeacherResponse> response = restTemplate.postForEntity(
                url(""),
                request,
                TeacherResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.name()).isEqualTo("Татьяна");
        assertThat(body.surname()).isEqualTo("Каратаева");
        assertThat(body.id()).isNotNull();
    }

    @Test
    void shouldGetTeacherById() {
        Teacher teacher = teacherRepository.findAll().get(0);

        ResponseEntity<TeacherResponse> response = restTemplate.getForEntity(
                url("/" + teacher.getId()),
                TeacherResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.name()).isEqualTo("Андрей");
        assertThat(body.surname()).isEqualTo("Андреев");
    }

    @Test
    void shouldReturn404WhenTeacherNotFound() {
        assertThatThrownBy(() -> restTemplate.getForEntity(url("/999"), String.class))
                .isInstanceOfSatisfying(HttpClientErrorException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void shouldGetAllTeachers() {
        ResponseEntity<PagedResponse> response = restTemplate.getForEntity(
                url("?page=0&size=10"),
                PagedResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PagedResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldUpdateTeacher() {
        Teacher teacher = teacherRepository.findAll().get(0);
        TeacherRequest request = new TeacherRequest("Обновлённый", "Учитель");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TeacherRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TeacherResponse> response = restTemplate.exchange(
                url("/" + teacher.getId()),
                HttpMethod.PUT,
                entity,
                TeacherResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.name()).isEqualTo("Обновлённый");
        assertThat(body.surname()).isEqualTo("Учитель");
    }

    @Test
    void shouldDeleteTeacher() {
        Teacher teacher = teacherRepository.findAll().get(0);

        restTemplate.delete(url("/" + teacher.getId()));

        long count = teacherRepository.count();
        assertThat(count).isZero();
    }
}