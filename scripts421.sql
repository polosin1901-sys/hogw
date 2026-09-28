ALTER TABLE hogwarts_student (
    age INTEGER CHECK (age >= 16) DEFAULT 20,
    name TEXT UNIQUE NOT NULL)

ALTER TABLE hogwarts_faculty(
    UNIQUE (name,color))