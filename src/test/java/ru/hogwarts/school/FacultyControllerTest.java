package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controllers.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    FacultyController facultyController;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() throws Exception {
        assertThat(facultyController).isNotNull();
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/faculty", String.class))
                .isNotNull();
    }

    @Test
    public void testGetFaculty() throws Exception {
        Faculty faculty = new Faculty(null,"vdvdv","fkfkfk");
        facultyRepository.save(faculty);
        String url = "http://localhost:" + port + "/faculty/" + faculty.getId();
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();
        Long nonExistenceFacultyId = 99L;
        String url2 = "http://localhost:" + port + "/faculty/" + nonExistenceFacultyId;
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentsFaculty() throws Exception {
        Faculty faculty = new Faculty(null,"vdvdv","fkfkfk");
        facultyRepository.save(faculty);
        Student student = new Student(null,"vdvd",17);
        Student student1 = new Student(null,"vdvrtd",18);
        student.setFaculty(faculty);
        student1.setFaculty(faculty);
        studentRepository.save(student);
        studentRepository.save(student1);
        facultyRepository.save(faculty);

        String url = "http://localhost:" + port + "/faculty/students/" + faculty.getId();
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        Faculty faculty2 = new Faculty(null,"vdvfddv","fkfkfk");
        facultyRepository.save(faculty2);
        String url2 = "http://localhost:" + port + "/faculty/students/" + faculty2.getId();
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetFacultiesByColor() throws Exception {
        String facultyColor = "зелёный";
        String url = "http://localhost:" + port + "/faculty/by-color?color=" + facultyColor;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();
        assertThat(this.testRestTemplate.getForObject(url, String.class).contains(facultyColor));


        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String nonExistenceFacultyColor = "серо-буро-малиновый";
        String url2 = "http://localhost:" + port + "/faculty/by-color?color=" + nonExistenceFacultyColor;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualTo("[]");
    }

    @Test
    public void testGetFacultiesByColorOrNameIgnoreCase() throws Exception {
        String facultyColor = "зелёный";
        String facultyName = "Гриффиндор";
        String url = "http://localhost:" + port + "/faculty/by-color-or-name-ignore-case?color=" + facultyColor + "&name=" + facultyName;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();
        String result = this.testRestTemplate.getForObject(url, String.class);
        assertThat
                (result.contains(facultyColor) || result.contains(facultyName));

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String nonExistenceFacultyColor = "серо-буро-малиновый";
        String nonExistenceFacultyName = "Балос";
        String url2 = "http://localhost:" + port + "/faculty/by-color-or-name-ignore-case?color=" + nonExistenceFacultyColor + "&name=" + nonExistenceFacultyName;
        ;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualTo("[]");
    }

    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Слизерин", "зелёный");
        String url = "http://localhost:" + port + "/faculty";
        ResponseEntity<String> requestEntity = this.testRestTemplate.postForEntity(url,faculty, String.class);
        assertThat(requestEntity.getBody()).isNotNull();
    }

    @Test
    public void testUpdateFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Слизерин", "зелёный");
        String url = "http://localhost:" + port + "/faculty";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(faculty, headers);

        ResponseEntity<Faculty> response =  this.testRestTemplate.exchange(url,HttpMethod.PUT,requestEntity,Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Слизерин");
        assertThat(response.getBody().getColor()).isEqualTo("зелёный");
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long facultyId = 1L;
        String url = "http://localhost:" + port + "/faculty/" + facultyId;

        ResponseEntity<Void> response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
