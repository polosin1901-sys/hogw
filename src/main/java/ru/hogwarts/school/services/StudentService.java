package ru.hogwarts.school.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private long lastId = 0;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student");
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            logger.error("No student found (in method getStudent) with id = {}", id);
        }
        return optionalStudent.orElse(null);
    }

    public Faculty getStudentsFaculty(Long id) {
        logger.info("Was invoked method for get students Faculty");
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            logger.error("No student found (in method getStudentsFaculty) with id = {}", id);
        }
        return optionalStudent.map(Student::getFaculty).orElse(null);
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student");
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for get students by age between");
        logger.debug("Searching for students with age between {} and {} ", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age = {}", age);
        return studentRepository.findByAge(age);
    }

    public Integer getAmountOfStudents() {
        logger.info("Was invoked method for get amount of students");
        return studentRepository.getAmountOfStudents();
    }

    public Double getAverageAgeOfStudents() {
        logger.info("Was invoked method for get average age of students");
        if (studentRepository.getAverageAgeOfStudents() == null) {
            logger.error("There are no students in the table");
            return 0.00;
        }
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<Student> getLast5Students() {
        logger.info("Was invoked method for get last 5 students");
        return studentRepository.getLast5Students();
    }

    public void getNameOfStudentsByParallelTread() {
        List<Student> students = new ArrayList<>(getAllStudents());
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread = new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        Thread thread1 = new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });
        thread.start();
        thread1.start();
    }

    private void printName(String name) {
        synchronized (StudentService.class) {
            System.out.println(name);
        }
    }

    public void getNameOfStudentsByParallelSynchronizedTread() {
        List<Student> students = new ArrayList<>(getAllStudents());
        printName(students.get(0).getName());
        printName(students.get(1).getName());

        Thread thread = new Thread(() -> {
            printName(students.get(2).getName());
            printName(students.get(3).getName());
        });

        Thread thread1 = new Thread(() -> {
            printName(students.get(4).getName());
            printName(students.get(5).getName());
        });
        thread.start();
        thread1.start();
    }
}
