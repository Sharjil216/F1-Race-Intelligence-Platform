ALTER TABLE meeting
    ADD COLUMN meeting_official_name TEXT,
    ADD COLUMN location TEXT,
    ADD COLUMN date_start TIMESTAMPTZ,
    ADD COLUMN date_end TIMESTAMPTZ;