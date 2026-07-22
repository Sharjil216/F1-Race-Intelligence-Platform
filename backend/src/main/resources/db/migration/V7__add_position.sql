CREATE TABLE driver_position (
    session_key INTEGER NOT NULL,
    driver_number INTEGER NOT NULL,
    position INTEGER NOT NULL,
    position_time TIMESTAMPTZ,
    FOREIGN KEY (session_key, driver_number) REFERENCES driver(session_key, driver_number),
    PRIMARY KEY (session_key, driver_number, position_time)
);