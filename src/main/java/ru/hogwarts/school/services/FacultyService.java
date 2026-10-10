package ru.hogwarts.school.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class FacultyService {


    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    private long lastId = 0;


    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty");
        Optional <Faculty> facultyOptional = facultyRepository.findById(id);
        if (!facultyOptional.isPresent()) {
            logger.error("No faculty found (in method getFaculty) with id = {}",id);
        }
        return facultyOptional.orElse(null);
    }

    public Collection<Student> getStudentsOfFaculty(Long id) {
        logger.info("Was invoked method for get students of faculty");
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        if (!optionalFaculty.isPresent()) {
            logger.error("No faculty found (in method getStudentsOfFaculty) with id = {}",id);
            return Collections.emptyList();
        }
        return optionalFaculty.orElse(null).getStudents();
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty");
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty");
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color = {}", color);
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultiesByColorOrNameIgnoreCase(String color, String name) {
        logger.info("Was invoked method for get faculties by color or name ignore case");
        logger.debug("Searching for faculties (ignore case) with color = {} or name = {}", color, name);
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(color, name);
    }
}