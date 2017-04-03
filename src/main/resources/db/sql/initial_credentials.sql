CREATE TABLE initial_credentials(
  id SERIAL,
  password VARCHAR(60),
  initialized BOOLEAN NOT NULL
);