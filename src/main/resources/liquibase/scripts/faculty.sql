-- liquibase formatted sql

--changeset npolosin:2
CREATE INDEX name_color_index ON hogwarts_faculty (name,color);