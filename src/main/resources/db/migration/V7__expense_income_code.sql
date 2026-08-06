-- Código único (letras + números) para egresos e ingresos manuales, igual que los trabajos.
CREATE SEQUENCE expense_consecutive_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE income_consecutive_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE expenses ADD COLUMN code VARCHAR(20);
ALTER TABLE manual_incomes ADD COLUMN code VARCHAR(20);

UPDATE expenses SET code = 'EG-' || LPAD(nextval('expense_consecutive_seq')::text, 4, '0') WHERE code IS NULL;
UPDATE manual_incomes SET code = 'IN-' || LPAD(nextval('income_consecutive_seq')::text, 4, '0') WHERE code IS NULL;

ALTER TABLE expenses ALTER COLUMN code SET NOT NULL;
ALTER TABLE manual_incomes ALTER COLUMN code SET NOT NULL;

ALTER TABLE expenses ADD CONSTRAINT uq_expenses_code UNIQUE (code);
ALTER TABLE manual_incomes ADD CONSTRAINT uq_manual_incomes_code UNIQUE (code);
