CREATE TABLE initial_credentials(
  id SERIAL,
  unique_row BOOLEAN NOT NULL,
  password VARCHAR(20),
  initialized BOOLEAN NOT NULL
);

ALTER TABLE initial_credentials
ADD CONSTRAINT initial_credentials_uc_unique_row UNIQUE (unique_row);