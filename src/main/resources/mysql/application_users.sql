CREATE TABLE application_users(
  id SERIAL,
  username VARCHAR(60) NOT NULL,
  password VARCHAR(60) NOT NULL,
  enabled BOOLEAN NOT NULL,
  first_name VARCHAR(60) NOT NULL,
  last_name VARCHAR(60) NOT NULL,
  user_type VARCHAR(20) NOT NULL,
  registration_time TIMESTAMP NOT NULL
);

ALTER TABLE application_users
ADD CONSTRAINT application_users_uc_username UNIQUE (username);