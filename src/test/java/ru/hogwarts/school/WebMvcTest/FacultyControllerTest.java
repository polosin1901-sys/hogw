package ru.hogwarts.school.WebMvcTest;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controllers.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.services.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    private Student makeStudent(long id, String name, int age) {
        Student s = new Student(id, name, age);
        return s;
    }

    private Faculty makeFaculty(long id, String name, String color) {
        Faculty f = new Faculty(id, name, color);
        return f;
    }

    @Test
    public void getAllFacultiesTest() throws Exception {
        when(facultyService.getAllFaculties())
                .thenReturn(List.of(makeFaculty(1L, "Gryffindor", "red"),
                        makeFaculty(2L, "Slytherin", "green")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"));
    }

    @Test
    public void getFacultyTest() throws Exception {
        when(facultyService.getFaculty(eq(1L)))
                .thenReturn(makeFaculty(1L, "Gryffindor", "red"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    public void getFacultyNotFoundTest() throws Exception {
        when(facultyService.getFaculty(eq(999L))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentsFacultyTest() throws Exception {
        when(facultyService.getStudentsOfFaculty(eq(1L)))
                .thenReturn(List.of(makeStudent(1L, "Bob", 16),
                        makeStudent(2L, "Alice", 17)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/students/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[1].name").value("Alice"));
    }

    @Test
    public void getStudentsFacultyNotFoundTest() throws Exception {
        when(facultyService.getStudentsOfFaculty(eq(999L))).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/students/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFacultiesByColorTest() throws Exception {
        when(facultyService.getFacultiesByColor("red"))
                .thenReturn(List.of(makeFaculty(1L, "Gryffindor", "red")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color")
                        .param("color", "red")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("red"));
    }

    @Test
    public void getFacultiesByColorBlankTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color")
                        .param("color", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getFacultiesByColorMissingTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getFacultiesByColorOrNameIgnoreCaseTest() throws Exception {
        when(facultyService.getFacultiesByColorOrNameIgnoreCase("red", "gr"))
                .thenReturn(List.of(makeFaculty(1L, "Gryffindor", "red")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color-or-name-ignore-case")
                        .param("color", "red")
                        .param("name", "gr")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    public void getFacultiesByColorOrNameIgnoreCaseBlankTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color-or-name-ignore-case")
                        .param("color", "")
                        .param("name", "gr")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void createFacultyTest() throws Exception {
        when(facultyService.createFaculty(any(Faculty.class)))
                .thenReturn(makeFaculty(1L, "Gryffindor", "red"));

        JSONObject json = new JSONObject();
        json.put("name", "Gryffindor");
        json.put("color", "red");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    public void updateFacultyTest() throws Exception {
        when(facultyService.updateFaculty(any(Faculty.class)))
                .thenReturn(makeFaculty(1L, "Gryffindor", "gold"));

        JSONObject json = new JSONObject();
        json.put("id", 1);
        json.put("name", "Gryffindor");
        json.put("color", "gold");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("gold"));
    }

    @Test
    public void updateFacultyBadRequestTest() throws Exception {
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(null);

        JSONObject json = new JSONObject();
        json.put("id", 999);
        json.put("name", "Ghost");
        json.put("color", "black");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        doNothing().when(facultyService).deleteFaculty(eq(1L));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1"))
                .andExpect(status().isOk());
    }
}
