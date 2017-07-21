CREATE TABLE initial_credentials(
  unique_row BOOLEAN NOT NULL,
  password VARCHAR(20),
  initialized BOOLEAN NOT NULL,
  PRIMARY KEY(unique_row)
);

ALTER TABLE initial_credentials
ADD CONSTRAINT initial_credentials_uc_unique_row UNIQUE (unique_row);