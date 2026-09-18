package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school.controllers.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import tools.jackson.databind.ObjectMapper;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    StudentController studentController;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void testGetAllStudents() throws Exception {
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/student", String.class))
                .isNotNull();
    }

    @Test
    public void testGetStudent() throws Exception {
        Student student = new Student(null,"rhfh",19);
        studentRepository.save(student);
        String url = "http://localhost:" + port + "/student/" + student.getId();
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        Long nonExistenceId = 99L;
        String url2 = "http://localhost:" + port + "/student/" + nonExistenceId;
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url2,String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentsFaculty() throws Exception {
        Student student = new Student(null,"rhfh",19);
        Faculty faculty = new Faculty(null,"ffff","erjkd");
        student.setFaculty(faculty);
        facultyRepository.save(faculty);
        studentRepository.save(student);

        String url = "http://localhost:" + port + "/student/faculty/" + student.getId();
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        Long nonExistenceId = 99L;
        String url2 = "http://localhost:" + port + "/student/faculty/" + nonExistenceId;
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url2,String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentByAgeBetween() throws Exception {
        int studentAgeMin = 16;
        int studentAgeMax = 26;
        String url = "http://localhost:" + port + "/student/age?min=" + studentAgeMin + "&max=" + studentAgeMax;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        int nonExistenceStudentMinAge = 80;
        int nonExistenceStudentMaxAge = 100;
        String url2 = "http://localhost:" + port + "/student/age?min=" + nonExistenceStudentMinAge + "&max=" + nonExistenceStudentMaxAge;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        int studentAge = 20;
        String url = "http://localhost:" + port + "/student/by-age?age=" + studentAge;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        int nonExistenceStudentAge = 80;
        String url2 = "http://localhost:" + port + "/student/by-age?age=" + nonExistenceStudentAge;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualTo("[]");
    }

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student(1L, "Sasha", 20);
        String url = "http://localhost:" + port + "/student";
        assertThat(this.testRestTemplate.postForObject(url, student, String.class)).isNotNull();
    }


    @Test
    public void testUpdateStudent() throws Exception {
        Student student = new Student(null, "Sasha", 20);
        String url = "http://localhost:" + port + "/student";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);

        ResponseEntity<Student> response = this.testRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(student.getName());
        assertThat(response.getBody().getAge()).isEqualTo(student.getAge());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long studentId = 1L;
        String url = "http://localhost:" + port + "/student/" + studentId;

        ResponseEntity<Void> response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


}
