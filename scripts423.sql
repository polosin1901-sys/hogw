SELECT hogwarts_student.name, hogwarts_student.age
FROM hogwarts_student
INNER JOIN hogwarts_faculty ON hogwarts_student.faculty_id = hogwarts_faculty.id

SELECT hogwarts_student.name, hogwarts_student.age
FROM hogwarts_student
INNER JOIN hogwarts_avatar ON hogwarts_student.avatar_id = hogwarts_avatar.id