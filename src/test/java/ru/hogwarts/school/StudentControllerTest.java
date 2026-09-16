package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school.controllers.StudentController;
import ru.hogwarts.school.model.Student;
import tools.jackson.databind.ObjectMapper;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    StudentController studentController;

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
        Long studentId = 1L;
        String url = "http://localhost:" + port + "/student/" + studentId;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Long nonExistenceStudentId = 9999L;
        String url2 = "http://localhost:" + port + "/student/" + nonExistenceStudentId;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentsFaculty() throws Exception {
        Long studentId = 1L;
        String url = "http://localhost:" + port + "/faculty/" + studentId;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Long nonExistenceStudentId = 9999L;
        String url2 = "http://localhost:" + port + "/faculty/" + nonExistenceStudentId;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentByAgeBetween() throws Exception {
        int studentAgeMin = 16;
        int studentAgeMax = 26;
        String url = "http://localhost:" + port + "/age?min=" + studentAgeMin + "&max=" + studentAgeMax;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        int nonExistenceStudentMinAge = 80;
        int nonExistenceStudentMaxAge = 100;
        String url2 = "http://localhost:" + port + "/age?min=" + nonExistenceStudentMinAge + "&max=" + nonExistenceStudentMaxAge;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        int studentAge = 20;
        String url = "http://localhost:" + port + "/by-age?age=" + studentAge;
        assertThat(this.testRestTemplate.getForObject(url, String.class)).isNotNull();

        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        int nonExistenceStudentAge = 80;
        String url2 = "http://localhost:" + port + "/by-age?age=" + nonExistenceStudentAge;

        ResponseEntity<String> response2 = this.testRestTemplate.getForEntity(url2, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualTo("[]");
    }

    @Test
    public void testDownloadAvatar() throws Exception {
        Long studentId = 1L;
        String url = "http://localhost:" + port + "/" + studentId + "/cover/data";
        assertThat(this.testRestTemplate.getForObject(url, byte[].class)).isNotNull();

        ResponseEntity<byte[]> response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Long nonExistenceStudentId = 999999L;
        String url2 = "http://localhost:" + port + "/" + nonExistenceStudentId + "/cover/data";

        ResponseEntity<byte[]> response2 = this.testRestTemplate.getForEntity(url2, byte[].class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDownloadAvatar2() throws Exception {
        Long studentId = 1L;
        String url = "http://localhost:" + port + "/" + studentId + "/cover";
        assertThat(this.testRestTemplate.getForObject(url, byte[].class)).isNotNull();

        ResponseEntity<byte[]> response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Long nonExistenceStudentId = 999999L;
        String url2 = "http://localhost:" + port + "/" + nonExistenceStudentId + "/cover";

        ResponseEntity<byte[]> response2 = this.testRestTemplate.getForEntity(url2, byte[].class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student(1L, "Sasha", 20);
        String url = "http://localhost:" + port + "/student";
        assertThat(this.testRestTemplate.postForObject(url, student, String.class)).isNotNull();
        ResponseEntity<String> response = this.testRestTemplate.postForEntity(url, student, String.class);
        Student response_ = new ObjectMapper().readValue(response.getBody(), Student.class);
        assertThat(response_.getAge()).isEqualTo(student.getAge());
        assertThat(response_.getName()).isEqualTo(student.getName());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testuploadAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "cover", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[1024 * 200]
        );
        String url = "http://localhost:" + port + "/1/cover";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cover", file.getResource());
        ResponseEntity<String> response = this.testRestTemplate.postForEntity(url, params, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MockMultipartFile largeFile = new MockMultipartFile(
                "cover", "large_avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[1024 * 400]
        );
        params.set("cover", largeFile.getResource());
        ResponseEntity<String> responseLarge = this.testRestTemplate.postForEntity(url, params, String.class);
        assertThat(responseLarge.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseLarge.getBody()).contains("File is too big");
    }

    @Test
    public void testUpdateStudent() throws Exception {
        Student student = new Student(1L, "Sasha", 20);
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
