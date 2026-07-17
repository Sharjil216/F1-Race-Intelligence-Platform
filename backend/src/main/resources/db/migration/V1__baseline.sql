CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE meeting (
    meeting_key INTEGER PRIMARY KEY,
    year INTEGER NOT NULL,
    meeting_name TEXT NOT NULL,
    country_name TEXT,
    circuit_short_name TEXT
);

CREATE TABLE session (
    session_key INTEGER PRIMARY KEY,
    meeting_key INTEGER NOT NULL REFERENCES meeting(meeting_key),
    session_name TEXT NOT NULL,
    session_type TEXT NOT NULL,
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ
);