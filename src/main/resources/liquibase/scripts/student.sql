-- liquibase formatted sql

--changeset npolosin:1
CREATE INDEX name_index ON hogwarts_student (name);
