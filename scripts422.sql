CREATE TABLE persons
(
    id              SERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    age             INTEGER CHECK (age > 0),
    drivers_license BOOLEAN,
    cars_id         TEXT,
    FOREIGN KEY (cars_id) REFERENCES cars (car_id)
)

CREATE TABLE cars
(
    car_id SERIAL PRIMARY KEY,
    car_brand TEXT NOT NULL,
    model     TEXT NOT NULL,
    price     INTEGER CHECK (price > 0)
)