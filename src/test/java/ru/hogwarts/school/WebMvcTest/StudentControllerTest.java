package ru.hogwarts.school.WebMvcTest;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controllers.StudentController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.services.AvatarService;
import ru.hogwarts.school.services.StudentService;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private AvatarService avatarService;

    private Student makeStudent(long id, String name, int age) {
        Student s = new Student(id, name, age);
        return s;
    }

    private Faculty makeFaculty(long id, String name, String color) {
        Faculty f = new Faculty(id, name, color);
        return f;
    }

    @Test
    public void getAllStudentsTest() throws Exception {
        when(studentService.getAllStudents())
                .thenReturn(List.of(makeStudent(1L, "Bob", 16),
                        makeStudent(2L, "Alice", 17)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[0].age").value(16))
                .andExpect(jsonPath("$[1].name").value("Alice"));
    }

    @Test
    public void getStudentTest() throws Exception {

        when(studentService.getStudent(eq(1L)))
                .thenReturn(makeStudent(1L, "Bob", 16));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.age").value(16));
    }

    @Test
    public void getStudentNotFoundTest() throws Exception {
        when(studentService.getStudent(eq(99L))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentsFacultyTest() throws Exception {
        when(studentService.getStudentsFaculty(eq(1L)))
                .thenReturn(makeFaculty(5L, "gjgjgj", "fkfkfk"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("gjgjgj"))
                .andExpect(jsonPath("$.color").value("fkfkfk"));
    }

    @Test
    public void getStudentsFacultyNotFoundTest() throws Exception {
        when(studentService.getStudentsFaculty(eq(999L))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentByAgeBetweenTest() throws Exception {
        when(studentService.getStudentsByAgeBetween(16, 18))
                .thenReturn(List.of(makeStudent(1L, "Bob", 16),
                        makeStudent(2L, "Alice", 17)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age")
                        .param("min", "16")
                        .param("max", "18")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[1].name").value("Alice"));
    }

    @Test
    public void getStudentByAgeBetweenNotFoundTest() throws Exception {
        when(studentService.getStudentsByAgeBetween(100, 200)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age")
                        .param("min", "100")
                        .param("max", "200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findStudentsByAgeTest() throws Exception {
        when(studentService.findByAge(16))
                .thenReturn(List.of(makeStudent(1L, "Bob", 16)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/by-age")
                        .param("age", "16")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bob"));
    }

    @Test
    public void findStudentsByAgeNonPositiveTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/by-age")
                        .param("age", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void downloadAvatarDataTest() throws Exception {
        byte[] data = "avatar-bytes".getBytes();

        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        avatar.setData(data);

        when(avatarService.findAvatar(eq(1L))).thenReturn(avatar);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1/cover/data"))
                .andExpect(status().isOk());
    }

    @Test
    public void downloadAvatarTest(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("avatar.png");
        Files.write(file, "avatar-bytes".getBytes());

        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        avatar.setFilePath(file.toString());
        avatar.setFileSize((int) Files.size(file));

        when(avatarService.findAvatar(eq(1L))).thenReturn(avatar);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/cover"))
                .andExpect(status().isOk());
    }

    @Test
    public void saveStudentTest() throws Exception {
        final long id = 1L;
        final String name = "Bob";
        final int age = 16;

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);

        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void uploadAvatarTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "cover",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "small-bytes".getBytes());

        doNothing().when(avatarService).uploadAvatar(eq(1L), any());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/student/1/cover")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void uploadAvatarTooBigTest() throws Exception {
        byte[] bigFile = new byte[1024 * 301];

        MockMultipartFile file = new MockMultipartFile(
                "cover",
                "big.png",
                MediaType.IMAGE_PNG_VALUE,
                bigFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/student/1/cover")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateStudentTest() throws Exception {
        when(studentService.updateStudent(any(Student.class)))
                .thenReturn(makeStudent(1L, "Bob", 17));

        JSONObject json = new JSONObject();
        json.put("id", 1);
        json.put("name", "Bob");
        json.put("age", 17);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    public void updateStudentBadRequestTest() throws Exception {
        when(studentService.updateStudent(any(Student.class))).thenReturn(null);

        JSONObject json = new JSONObject();
        json.put("id", 999);
        json.put("name", "Ghost");
        json.put("age", 20);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteStudentTest() throws Exception {
        // deleteStudent у сервиса — void
        doNothing().when(studentService).deleteStudent(eq(1L));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1"))
                .andExpect(status().isOk());
    }
}
