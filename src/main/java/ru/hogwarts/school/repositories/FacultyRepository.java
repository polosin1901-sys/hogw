package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty,Long> {
    Collection<Faculty> findByColor(String color);

    Collection<Faculty> findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String color, String name);
}
