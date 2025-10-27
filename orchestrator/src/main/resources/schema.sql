-- Initial schema for saga storage
CREATE TABLE IF NOT EXISTS saga (
  saga_id varchar(100) PRIMARY KEY,
  status varchar(50) NOT NULL,
  created_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS saga_step (
  id bigserial PRIMARY KEY,
  saga_id varchar(100) NOT NULL REFERENCES saga(saga_id) ON DELETE CASCADE,
  step_name varchar(100) NOT NULL,
  status varchar(50) NOT NULL,
  payload text,
  compensation varchar(100)
);
