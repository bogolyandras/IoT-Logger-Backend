CREATE TABLE device_logs(
  id SERIAL,
  device_id BIGINT UNSIGNED NOT NULL,
  data_time TIMESTAMP NOT NULL,
  metric_1 DECIMAL (20, 5),
  metric_2 DECIMAL (20, 5),
  metric_3 DECIMAL (20, 5),

  PRIMARY KEY (id),
  FOREIGN KEY (device_id)
          REFERENCES devices(id)
          ON DELETE CASCADE
          ON UPDATE CASCADE,
  INDEX(device_id, data_time)
);
