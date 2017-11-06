CREATE TABLE devices(
  id SERIAL,
  owner_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(60) NOT NULL,
  description VARCHAR(1024) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (owner_id)
          REFERENCES application_users(id)
          ON DELETE CASCADE
          ON UPDATE CASCADE
);
