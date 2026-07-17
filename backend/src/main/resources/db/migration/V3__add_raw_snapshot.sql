CREATE TABLE raw_snapshot (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    endpoint TEXT NOT NULL,
    fetched_at TIMESTAMPTZ DEFAULT NOW(),
    payload JSONB NOT NULL,
    query_params TEXT
);