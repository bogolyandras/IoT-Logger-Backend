CREATE TABLE application_properties(
  property_key VARCHAR(60) NOT NULL UNIQUE,
  value VARCHAR(20),

  PRIMARY KEY(property_key)
);
