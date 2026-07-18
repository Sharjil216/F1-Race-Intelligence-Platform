CREATE TABLE lap (
    lap_number INTEGER NOT NULL,
    driver_number INTEGER NOT NULL,
    session_key INTEGER NOT NULL,
    date_start TIMESTAMPTZ,
    lap_duration NUMERIC(9,3),
    duration_sector_1 NUMERIC(9,3),
    duration_sector_2 NUMERIC(9,3),
    duration_sector_3 NUMERIC(9,3),
    i1_speed INTEGER,
    i2_speed INTEGER,
    st_speed INTEGER,
    is_pit_out_lap BOOLEAN,
    FOREIGN KEY (session_key, driver_number) REFERENCES driver(session_key, driver_number),
    PRIMARY KEY (session_key, driver_number, lap_number)
);