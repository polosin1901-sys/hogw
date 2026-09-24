package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeBetween(int min, int max);

    @Query(value = "SELECT COUNT(*) FROM hogwarts_student", nativeQuery = true)
    Integer getAmountOfStudents();

    @Query(value = "SELECT AVG(age) FROM hogwarts_student", nativeQuery = true)
    Double getAverageAgeOfStudents();

    @Query(value = "SELECT * FROM hogwarts_student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getLast5Students();
}
