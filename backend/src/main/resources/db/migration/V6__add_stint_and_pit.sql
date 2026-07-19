CREATE TABLE stint (
    stint_number INTEGER NOT NULL,
    session_key INTEGER NOT NULL,
    driver_number INTEGER NOT NULL,
    lap_start INTEGER,
    lap_end INTEGER,
    compound TEXT,
    tyre_age_at_start INTEGER,
    FOREIGN KEY (session_key, driver_number) REFERENCES driver(session_key, driver_number),
    PRIMARY KEY (session_key, driver_number, stint_number)
);

CREATE TABLE pit (
    lap_number INTEGER NOT NULL,
    session_key INTEGER NOT NULL,
    driver_number INTEGER NOT NULL,
    pit_time TIMESTAMPTZ,
    stop_duration NUMERIC(9,3),
    pit_duration NUMERIC(9,3),
    lane_duration NUMERIC(9,3),
    FOREIGN KEY (session_key, driver_number) REFERENCES driver(session_key, driver_number),
    PRIMARY KEY (session_key, driver_number, lap_number)
);