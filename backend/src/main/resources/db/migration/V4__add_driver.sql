CREATE TABLE driver (
    driver_number INTEGER NOT NULL,
    session_key INTEGER NOT NULL REFERENCES session(session_key),
    full_name TEXT NOT NULL,
    name_acronym TEXT,
    team_name TEXT NOT NULL,
    country_code TEXT,
    team_colour TEXT,
    PRIMARY KEY (session_key, driver_number)
);