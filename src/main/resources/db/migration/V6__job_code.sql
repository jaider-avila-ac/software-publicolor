-- Código único del trabajo (letras + números), independiente del título.
-- Se guarda en su propia columna y se verifica que no se repita entre trabajos.
ALTER TABLE jobs ADD COLUMN code VARCHAR(20);

UPDATE jobs SET code = 'OT-' || LPAD(consecutive_number::text, 4, '0') WHERE code IS NULL;

ALTER TABLE jobs ALTER COLUMN code SET NOT NULL;
ALTER TABLE jobs ADD CONSTRAINT uq_jobs_code UNIQUE (code);
